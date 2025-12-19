import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.io.Serializable;

public class Medical_Document implements Serializable {
    private String docId;
    private String recordId;
    private String patientId;
    private Date date;
    private String diagnosis;
    private String treatment;
    private String symptoms;
    private String doctorId;
    private String appointmentId;
    private ArrayList<String> history;
    
    public Medical_Document(String patientId, Date date, String diagnosis, String treatment, String symptoms, String doctorId, String appointmentId) {
        this.docId = IdGenerator.generateDocumentId();
        this.recordId = IdGenerator.generateRecordId();
        this.patientId = patientId;
        this.date = new Date();
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.symptoms = symptoms;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.history = new ArrayList<>();
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }
    public void updateDocument(String docId, String patientId, Date date) {
        setDocId(docId);
        setPatientId(patientId);
        setDate(date);
    }

    public void addRecord(String entry) {
        history.add(entry);
    }

    public void updateRecord(String diagnosis, String treatment, String symptoms) {
        setDiagnosis(diagnosis);
        setTreatment(treatment);
        setSymptoms(symptoms);
        addRecord("Updated - Diagnosis: " + diagnosis + ", Treatment: " + treatment + ", Symptoms: " + symptoms);
    }

    public void generateReport() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        System.out.println("Medical Report:");
        System.out.println("Document ID: " + docId);
        System.out.println("Record ID: " + recordId);
        System.out.println("Patient ID: " + patientId);
        System.out.println("Date: " + sdf.format(date));
        System.out.println("Diagnosis: " + diagnosis);
        System.out.println("Treatment: " + treatment);
        System.out.println("Symptoms: " + symptoms);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("History:");
        for (String entry : history) {
            System.out.println("  - " + entry);
        }
    }

    public String getDocumentDetails() {
        return "Medical_Document{" +
                "docId='" + docId + '\'' +
                ", recordId='" + recordId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", date=" + date +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", historySize=" + history.size() +
                '}';
    }
}
