import java.io.*;
import java.util.ArrayList;

/**
 * Simple FileManager - Handles saving and loading clinic data to files
 * This class helps save doctors, patients, and appointments information
 */
public class FileManager {
    // Where we store our files
    private static final String DATA_FOLDER = "data/";
    private static final String CLINIC_FILE = DATA_FOLDER + "clinic.dat";
    private static final String DOCTORS_FILE = DATA_FOLDER + "doctors.dat";
    private static final String PATIENTS_FILE = DATA_FOLDER + "patients.dat";
    private static final String APPOINTMENTS_FILE = DATA_FOLDER + "appointments.dat";

    /**
     * Create the data folder if it doesn't exist
     */
    public static void initializeDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("Data folder created.");
        }
    }

    // ========== SAVE METHODS ==========

    /**
     * Save clinic information to file
     */
    public static void saveClinic(Clinic clinic) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLINIC_FILE))) {
            oos.writeObject(clinic);
            System.out.println("Clinic saved!");
        } catch (IOException e) {
            System.out.println("Error saving clinic: " + e.getMessage());
        }
    }

    /**
     * Save doctors list to file
     */
    public static void saveDoctors(ArrayList<Doctor> doctors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DOCTORS_FILE))) {
            oos.writeObject(doctors);
            System.out.println("Doctors saved! (" + doctors.size() + " doctors)");
        } catch (IOException e) {
            System.out.println("Error saving doctors: " + e.getMessage());
        }
    }

    /**
     * Save patients list to file
     */
    public static void savePatients(ArrayList<Patient> patients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATIENTS_FILE))) {
            oos.writeObject(patients);
            System.out.println("Patients saved! (" + patients.size() + " patients)");
        } catch (IOException e) {
            System.out.println("Error saving patients: " + e.getMessage());
        }
    }

    /**
     * Save appointments list to file
     */
    public static void saveAppointments(ArrayList<Appointment> appointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPOINTMENTS_FILE))) {
            oos.writeObject(appointments);
            System.out.println("Appointments saved! (" + appointments.size() + " appointments)");
        } catch (IOException e) {
            System.out.println("Error saving appointments: " + e.getMessage());
        }
    }

    /**
     * Save everything at once
     */
    public static void saveAll(Clinic clinic) {
        System.out.println("\n=== Saving all data ===");
        saveClinic(clinic);
        saveDoctors(clinic.getDoctors());
        savePatients(clinic.getPatients());
        saveAppointments(clinic.getAppointments());
        System.out.println("All data saved!\n");
    }

    // ========== LOAD METHODS ==========

    /**
     * Load clinic information from file
     */
    public static Clinic loadClinic() {
        File file = new File(CLINIC_FILE);
        if (!file.exists()) {
            System.out.println("No clinic file found.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CLINIC_FILE))) {
            Clinic clinic = (Clinic) ois.readObject();
            System.out.println("Clinic loaded!");
            return clinic;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading clinic: " + e.getMessage());
            return null;
        }
    }

    /**
     * Load doctors list from file
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Doctor> loadDoctors() {
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) {
            System.out.println("No doctors file found.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DOCTORS_FILE))) {
            ArrayList<Doctor> doctors = (ArrayList<Doctor>) ois.readObject();
            System.out.println("Doctors loaded! (" + doctors.size() + " doctors)");
            return doctors;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading doctors: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Load patients list from file
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Patient> loadPatients() {
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            System.out.println("No patients file found.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATIENTS_FILE))) {
            ArrayList<Patient> patients = (ArrayList<Patient>) ois.readObject();
            System.out.println("Patients loaded! (" + patients.size() + " patients)");
            return patients;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading patients: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Load appointments list from file
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Appointment> loadAppointments() {
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            System.out.println("No appointments file found.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(APPOINTMENTS_FILE))) {
            ArrayList<Appointment> appointments = (ArrayList<Appointment>) ois.readObject();
            System.out.println("Appointments loaded! (" + appointments.size() + " appointments)");
            return appointments;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading appointments: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}