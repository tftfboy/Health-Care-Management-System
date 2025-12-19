import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class Appointment implements Serializable {
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private Date date;
    private String status;
    private String reason;
    private boolean hasCheckup;

    public Appointment(Patient patient, Doctor doctor, Date date, String status, String reason) {
        this.appointmentId = IdGenerator.generateAppointmentId();
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
        this.status = status;
        this.reason = reason;
        this.hasCheckup = false;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAppointmentDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patientId='" + patient.getId() + '\'' +
                ", patientName='" + patient.getName() + '\'' +
                ", doctorId='" + doctor.getId() + '\'' +
                ", doctorName='" + doctor.getName() + '\'' +
                ", date=" + dateFormat.format(date) +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

    public void scheduleAppointment() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.status = "SCHEDULED";
        System.out.println("Appointment ID: " + appointmentId + " has been scheduled for " + patient.getName() + " with Dr. " + doctor.getName() + " on " + dateFormat.format(date));
    }

    public void cancelAppointment() {
        this.status = "CANCELLED";
        System.out.println("Appointment ID: " + appointmentId + " has been cancelled.");
    }

    public void rescheduleAppointment(Date newDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.date = newDate;
        this.status = "RESCHEDULED";
        System.out.println("Appointment ID: " + appointmentId + " has been rescheduled to " + dateFormat.format(newDate));
    }

    public boolean hasCheckup() {
        return hasCheckup;
    }

    public void setHasCheckup(boolean hasCheckup) {
        this.hasCheckup = hasCheckup;
    }

    public boolean completeAppointment() {
        if (!hasCheckup) {
            System.out.println("Cannot complete appointment. Medical checkup/report required.");
            return false;
        }
        this.status = "COMPLETED";
        System.out.println("Appointment ID: " + appointmentId + " has been marked as completed.");
        return true;
    }

}