import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class FinancialService implements Serializable {
    private String id;
    private Patient patient; 
    private double amount = 0;
    private String invoiceId;
    private double totalAmount = 0;
    private Date invoiceDate;
    private String status;
    private String paymentMethod;
    private ArrayList<String> items;
    
    public FinancialService(Patient patient, double amount, double totalAmount, String status, String paymentMethod) {
        this.id = IdGenerator.generateFinancialServiceId();
        this.patient = patient;
        this.amount = amount;
        this.invoiceId = IdGenerator.generateInvoiceId();
        this.totalAmount = totalAmount;
        this.invoiceDate = new Date();
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public boolean isValidPaymentMethod(String method) {
        if (method == null || method.isEmpty()) {
            return false;
        }
        String upperMethod = method.toUpperCase();
        return upperMethod.equals("VISA") || upperMethod.equals("CASH");
    }

    public void addItem(String item) {
        items.add(item);
    }

    public double calculateBill() {
        return totalAmount + amount;
    }

    public void processPayment(double paymentAmount) {
        if (!isValidPaymentMethod(paymentMethod)) {
            setStatus("Payment Rejected - Invalid Payment Method");
            return;
        }
        if (paymentAmount >= totalAmount) {
            setStatus("Paid");
        } else {
            setStatus("Partial Payment");
        }
    }

    public String generateInvoice() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", patientName='" + (patient != null ? patient.getName() : "null") + '\'' +
                ", totalAmount=" + totalAmount +
                ", invoiceDate='" + dateFormat.format(invoiceDate) + '\'' +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }

    public String trackPayment() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Payment Tracking:\n" +
                "Invoice ID: " + invoiceId + "\n" +
                "Patient: " + (patient != null ? patient.getName() : "null") + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n" +
                "Payment Date: " + dateFormat.format(invoiceDate) + "\n" +
                "Status: " + status + "\n" +
                "Payment Method: " + paymentMethod;
    }

    public double calcTotal(double... amounts) {
        double total = this.amount;
        for (double amt : amounts) {
            total += amt;
        }
        return total;
    }

    public String generateFinancialReport() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Financial Report:\n" +
                "Service ID: " + id + "\n" +
                "Invoice ID: " + invoiceId + "\n" +
                "Patient: " + (patient != null ? patient.getName() : "null") + "\n" +
                "Total Amount: Rs. " + totalAmount + "\n" +
                "Invoice Date: " + dateFormat.format(invoiceDate) + "\n" +
                "Status: " + status + "\n" +
                "Items: " + items;
    }
}
