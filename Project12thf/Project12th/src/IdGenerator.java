public class IdGenerator {
    
    // Counters for generating unique IDs
    private static int patientCounter = 1000;
    private static int doctorCounter = 2000;
    private static int appointmentCounter = 3000;
    private static int medicalDocCounter = 4000;
    private static int financialCounter = 5000;
    private static int healthReportCounter = 6000;
    private static int invoiceCounter = 7000;
    private static int documentCounter = 8000;
    private static int clinicCounter = 9000;
    
    // ID Generation Methods with unique prefixes
    
    public static String generatePatientId() {
        return "PAT" + (++patientCounter);
    }
    
    public static String generateDoctorId() {
        return "DOC" + (++doctorCounter);
    }
    
    public static String generateAppointmentId() {
        return "APT" + (++appointmentCounter);
    }
    
    public static String generateMedicalDocumentId() {
        return "MED" + (++medicalDocCounter);
    }
    
    public static String generateFinancialServiceId() {
        return "FIN" + (++financialCounter);
    }
    
    public static String generateHealthReportId() {
        return "HLT" + (++healthReportCounter);
    }
    
    public static String generateInvoiceId() {
        return "INV" + (++invoiceCounter);
    }
    
    public static String generateRecordId() {
        return "REC" + (++medicalDocCounter);
    }
    
    public static String generateDocumentId() {
        return "DOC_ID" + (++documentCounter);
    }
    
    public static String generateClinicId() {
        return "CLINIC" + (++clinicCounter);
    }
    
    // Reset counters if needed (for testing purposes)
    public static void resetCounters() {
        patientCounter = 1000;
        doctorCounter = 2000;
        appointmentCounter = 3000;
        medicalDocCounter = 4000;
        financialCounter = 5000;
        healthReportCounter = 6000;
        invoiceCounter = 7000;
        documentCounter = 8000;
        clinicCounter = 9000;
    }
    
    // Get current counter values
    public static int getPatientCount() {
        return patientCounter;
    }
    
    public static int getDoctorCount() {
        return doctorCounter;
    }
    
    public static int getAppointmentCount() {
        return appointmentCounter;
    }
}
