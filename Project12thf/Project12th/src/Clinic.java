import java.util.ArrayList;
import java.io.Serializable;

public class Clinic implements Serializable {
    private String clinicId;
    private String name;
    private String address;
    private String phone;
    private ArrayList<Doctor> doctors;
    private ArrayList<Patient> patients;
    private ArrayList<Appointment> appointments;
    private ArrayList<FinancialService> financialServices;
    private ArrayList<Medical_Document> medicalDocuments;
    private ArrayList<Health_Report> healthReports;
    private AppointmentValidator appointmentValidator;

    public Clinic(String name, String address, String phone) {
        this.clinicId = IdGenerator.generateClinicId();
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.financialServices = new ArrayList<>();
        this.medicalDocuments = new ArrayList<>();
        this.healthReports = new ArrayList<>();
        this.appointmentValidator = new AppointmentCheckerImpl();
    }

    // Getters
    public String getClinicId() {
        return clinicId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public ArrayList<FinancialService> getFinancialServices() {
        return financialServices;
    }

    public void addFinancialService(FinancialService service) {
        financialServices.add(service);
    }

    public ArrayList<Medical_Document> getMedicalDocuments() {
        return medicalDocuments;
    }

    public void addMedicalDocument(Medical_Document document) {
        medicalDocuments.add(document);
    }

    public ArrayList<Health_Report> getHealthReports() {
        return healthReports;
    }

    public void addHealthReport(Health_Report report) {
        healthReports.add(report);
    }

    // Doctors Management
    public boolean isDoctorRegistered(String doctorId) {
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(doctorId)) {
                return true;
            }
        }
        return false;
    }

    public void addDoctor(Doctor doctor) {
        if (doctor != null) {
            if (isDoctorRegistered(doctor.getId())) {
                System.out.println("Error: Dr. " + doctor.getName() + " (ID: " + doctor.getId() + ") is already registered in the clinic!");
            } else {
                doctors.add(doctor);
                System.out.println("Dr. " + doctor.getName() + " added to the clinic successfully!");
            }
        } else {
            System.out.println("Error: Doctor cannot be null!");
        }
    }

    public void removeDoctor(String doctorId) {
        boolean found = false;
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equals(doctorId)) {
                String doctorName = doctors.get(i).getName();
                doctors.remove(i);
                System.out.println("Dr. " + doctorName + " removed from the clinic.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Error: Doctor with ID " + doctorId + " not found!");
        }
    }

    // Patients Management
    public boolean isPatientRegistered(String patientId) {
        for (Patient patient : patients) {
            if (patient.getId().equals(patientId)) {
                return true;
            }
        }
        return false;
    }

    public void registerPatient(Patient patient) {
        if (patient != null) {
            if (isPatientRegistered(patient.getId())) {
                System.out.println("Error: Patient " + patient.getName() + " (ID: " + patient.getId() + ") is already registered!");
            } else {
                patients.add(patient);
                System.out.println("Patient " + patient.getName() + " registered successfully!");
            }
        } else {
            System.out.println("Error: Patient cannot be null!");
        }
    }

    public void removePatient(String patientId) {
        boolean found = false;
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(patientId)) {
                String patientName = patients.get(i).getName();
                patients.remove(i);
                System.out.println("Patient " + patientName + " removed from the clinic.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Error: Patient with ID " + patientId + " not found!");
        }
    }

    // Appointments Management
    public void scheduleAppointment(Appointment appointment) {
        if (appointment != null) {
            if (appointmentValidator.isAppointmentExists(appointment, appointments)) {
                System.out.println("Appointment cannot be scheduled due to conflict!");
            } else {
                appointments.add(appointment);
                appointment.scheduleAppointment();
                System.out.println("Appointment scheduled successfully!");
            }
        } else {
            System.out.println("Error: Appointment cannot be null!");
        }
    }

    // Search Methods
    public Doctor searchDoctor(String doctorId) {
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(doctorId)) {
                return doctor;
            }
        }
        System.out.println("Error: Doctor with ID " + doctorId + " not found!");
        return null;
    }

    public Patient searchPatient(String patientId) {
        for (Patient patient : patients) {
            if (patient.getId().equals(patientId)) {
                return patient;
            }
        }
        System.out.println("Error: Patient with ID " + patientId + " not found!");
        return null;
    }

    // Display Methods
    public void viewAllDoctors() {
        System.out.println("\n========== ALL DOCTORS ==========");
        if (doctors.isEmpty()) {
            System.out.println("No doctors in the clinic.");
        } else {
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println("\n" + (i + 1) + ". " + doctors.get(i).getDoctorDetails());
            }
        }
        System.out.println("================================\n");
    }

    public void viewAllPatients() {
        System.out.println("\n========== ALL PATIENTS ==========");
        if (patients.isEmpty()) {
            System.out.println("No patients registered in the clinic.");
        } else {
            for (int i = 0; i < patients.size(); i++) {
                System.out.println("\n" + (i + 1) + ". ID: " + patients.get(i).getId() + 
                                   ", Name: " + patients.get(i).getName() + 
                                   ", Blood Type: " + patients.get(i).getBloodType());
            }
        }
        System.out.println("==================================\n");
    }

    public void displayAppointments() {
        System.out.println("\n========== ALL APPOINTMENTS ==========");
        if (appointments.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            for (int i = 0; i < appointments.size(); i++) {
                System.out.println("\n" + (i + 1) + ". " + appointments.get(i).getAppointmentDetails());
            }
        }
        System.out.println("======================================\n");
    }

    // Reports
    public String generateClinicReport() {
        // Calculate total revenue
        double totalRevenue = 0;
        for (FinancialService service : financialServices) {
            totalRevenue += service.getTotalAmount();
        }
        
        // Calculate appointment statistics
        int scheduled = 0, completed = 0, cancelled = 0;
        for (Appointment apt : appointments) {
            String status = apt.getStatus().toUpperCase();
            if (status.equals("SCHEDULED")) scheduled++;
            else if (status.equals("COMPLETED")) completed++;
            else if (status.equals("CANCELLED")) cancelled++;
        }
        
        return "\n========== CLINIC REPORT ==========\n" +
               "Clinic ID: " + clinicId + "\n" +
               "Name: " + name + "\n" +
               "Address: " + address + "\n" +
               "Phone: " + phone + "\n\n" +
               "--- STAFF & PATIENTS ---\n" +
               "Total Doctors: " + doctors.size() + "\n" +
               "Total Patients: " + patients.size() + "\n\n" +
               "--- APPOINTMENTS ---\n" +
               "Total Appointments: " + appointments.size() + "\n" +
               "  Scheduled: " + scheduled + "\n" +
               "  Completed: " + completed + "\n" +
               "  Cancelled: " + cancelled + "\n\n" +
               "--- FINANCIAL ---\n" +
               "Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n" +
               "Total Transactions: " + financialServices.size() + "\n\n" +
               "--- MEDICAL RECORDS ---\n" +
               "Health Reports: " + healthReports.size() + "\n" +
               "Medical Documents: " + medicalDocuments.size() + "\n" +
               "====================================\n";
    }

    public String getClinicDetails() {
        return "Clinic{" +
                "clinicId='" + clinicId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", doctors=" + doctors.size() +
                ", patients=" + patients.size() +
                ", appointments=" + appointments.size() +
                '}';
    }
}
