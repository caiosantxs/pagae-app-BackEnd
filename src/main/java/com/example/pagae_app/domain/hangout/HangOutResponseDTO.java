package com.example.pagae_app.domain.hangout;

import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import com.example.pagae_app.domain.payment.PaymentActivityDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "DTO for returning HangOut data")
public record HangOutResponseDTO(
        @Schema(description = "HangOut's unique identifier", example = "58")
        Long id,

        @Schema(description = "Title of the HangOut", example = "Viagem para a Praia")
        String title,

        @Schema(description = "Brief description of the HangOut", example = "Organização da viagem de fim de ano da turma.")
        String description,

        @Schema(description = "ID of the user who created the HangOut", example = "101")
        Long creatorId,

        LocalDate creationDate,

        StatusHangOut statusHangOut,

        @Schema(description = "Expenses from hangout")
        List<ExpenseResponseDTO> expenses,

        List<MemberDTO> members,

        List<PaymentActivityDTO> recentActivities
) {
    public HangOutResponseDTO(HangOut hangOut) {
        this(
                hangOut.getId(),
                hangOut.getTitle(),
                hangOut.getDescription(),
                hangOut.getCreator().getId(),
                hangOut.getCreationDate(),
                hangOut.getStatus(),
                hangOut.getExpenses().stream()
                        .map(ExpenseResponseDTO::new).collect(Collectors.toList()),
                hangOut.getMembers().stream().map(member -> new MemberDTO(
                        member.getUser().getId(),
                        member.getUser().getName()
                )).toList(),

                hangOut.getExpenses().stream()
                        .flatMap(expense -> expense.getPayments().stream()
                                .map(payment -> new PaymentActivityDTO(
                                        payment.getId(),

                                        // 1. Quem Pagou (Vem do Payment -> User)
                                        payment.getUser().getName(),

                                        // 2. Descrição (Vem do Payment -> Expense)
                                        // Se a despesa for "Pizza", o pagamento aparecerá como "Pizza"
                                        expense.getDescription(),

                                        // 3. Valor (Vem do Payment)
                                        payment.getAmount(),

                                        // 4. Data (Vem do Payment -> Expense)
                                        // Assumindo que Expense tem getDate(). Se for null, usa Agora.
                                        expense.getDate() != null ? expense.getDate().atStartOfDay() : LocalDateTime.now(),

                                        // 5. ID do pagador (Vem do Payment -> User)
                                        payment.getUser().getId()
                                ))
                        )
                        // Ordena: Do mais recente para o mais antigo
                        .sorted((p1, p2) -> p2.date().compareTo(p1.date()))
                        // Pega só os 5 últimos para não poluir a tela
                        .limit(5)
                        .toList()
        );
    }
}
