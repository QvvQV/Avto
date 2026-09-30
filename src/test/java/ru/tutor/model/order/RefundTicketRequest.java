package ru.tutor.model.order;

public class RefundTicketRequest {
    private String firstname;
    private String lastname;
    private String passport;
    private String patronymic;
    private String email;
    private String card;
    private String bank;
    private String paymentDate;
    private int paymentAmount;
    private String viewedCourses;
    private String refundReason;

    public RefundTicketRequest(
            String firstname, String lastname, String passport,
            String patronymic, String email, String card,
            String bank, String paymentDate, int paymentAmount, String viewedCourses,
            String refundReason
    ) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.passport = passport;
        this.patronymic = patronymic;
        this.email = email;
        this.card = card;
        this.bank = bank;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.viewedCourses = viewedCourses;
        this.refundReason = refundReason;
    }

    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getPassport() { return passport; }
    public String getPatronymic() { return patronymic; }
    public String getEmail() { return email; }
    public String getCard() { return card; }
    public String getBank() { return bank; }
    public String getPaymentDate() { return paymentDate; }
    public int getPaymentAmount() { return paymentAmount; }
    public String getViewedCourses() { return viewedCourses; }
    public String getRefundReason() { return refundReason; }
}
