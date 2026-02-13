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

        @Schema(description = "ID of the user who created the HangOut", example = "101")
        Long creatorId,

        LocalDate creationDate,

        StatusHangOut statusHangOut,

        boolean hasPendingDebts,

        @Schema(description = "Expenses from hangout")
        List<ExpenseResponseDTO> expenses,

        List<MemberDTO> members,

        List<PaymentActivityDTO> recentActivities
) {
    public HangOutResponseDTO(HangOut hangOut, boolean hasPendingDebts) {
        this(
                hangOut.getId(),
                hangOut.getTitle(),
                hangOut.getCreator().getId(),
                hangOut.getCreationDate(),
                hangOut.getStatus(),
                hasPendingDebts,
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
                                        payment.getUser().getName(),
                                        expense.getDescription(),
                                        payment.getAmount(),
                                        payment.getDate() != null ? payment.getDate().atStartOfDay() : LocalDateTime.now(),
                                        payment.getUser().getId()
                                ))
                        )
                        .sorted((p1, p2) -> p2.date().compareTo(p1.date()))
                        .limit(5)
                        .toList()
        );
    }

    public HangOutResponseDTO(HangOut hangOut) {
        this(hangOut, false);
    }
}
