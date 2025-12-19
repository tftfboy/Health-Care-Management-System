import java.util.ArrayList;

public class DoctorSchedule {
    private ArrayList<Doctor> doctors;
    

    public DoctorSchedule() {
        this.doctors = new ArrayList<>();
    }

    public void viewAllDoctorsAvailability() {
        System.out.println("========== ALL DOCTORS AVAILABILITY ==========");
        if (doctors.isEmpty()) {
            System.out.println("No doctors in the schedule.");
            return;
        }
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doc = doctors.get(i);
            System.out.println("\n" + (i + 1) + ". Dr. " + doc.getName());
            System.out.println("   Specialization: " + doc.getSpecialization());
            System.out.println("   Availability: " + doc.getAvailabilitySchedule());
            System.out.println("   License: " + doc.getLicenseNumber());
        }
        System.out.println("\n===========================================");
    }

    public ArrayList<Doctor> searchDoctorBySpecialization(String specialization) {
        ArrayList<Doctor> foundDoctors = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getSpecialization().equalsIgnoreCase(specialization)) {
                foundDoctors.add(doctors.get(i));
            }
        }
        return foundDoctors;
    }

    public void displayDoctorsBySpecialization(String specialization) {
        ArrayList<Doctor> foundDoctors = searchDoctorBySpecialization(specialization);
        System.out.println("========== DOCTORS - " + specialization.toUpperCase() + " ==========");
        if (foundDoctors.isEmpty()) {
            System.out.println("No doctors found for specialization: " + specialization);
            return;
        }
        for (int i = 0; i < foundDoctors.size(); i++) {
            Doctor doc = foundDoctors.get(i);
            System.out.println("\n" + (i + 1) + ". Dr. " + doc.getName());
            System.out.println("   Availability: " + doc.getAvailabilitySchedule());
            System.out.println("   Experience: " + doc.getYearsOfExperience() + " years");
        }
        System.out.println("\n=====================================");
    }

    public boolean checkDoctorAvailability(String doctorId, String day) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equals(doctorId)) {
                return doctors.get(i).isAvailable(day);
            }
        }
        return false;
    }

    public ArrayList<Doctor> getAvailableDoctors(String day) {
        ArrayList<Doctor> availableDoctors = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).isAvailable(day)) {
                availableDoctors.add(doctors.get(i));
            }
        }
        return availableDoctors;
    }

    public void displayAvailableDoctorsOnDay(String day) {
        ArrayList<Doctor> availableDoctors = getAvailableDoctors(day);
        System.out.println("========== AVAILABLE DOCTORS ON " + day.toUpperCase() + " ==========");
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors available on " + day);
            return;
        }
        for (int i = 0; i < availableDoctors.size(); i++) {
            Doctor doc = availableDoctors.get(i);
            System.out.println((i + 1) + ". Dr. " + doc.getName() + " (" + doc.getSpecialization() + ")");
        }
        System.out.println("=====================================");
    }

    public Doctor getDoctorById(String doctorId) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equals(doctorId)) {
                return doctors.get(i);
            }
        }
        return null;
    }

    public int getTotalDoctors() {
        return doctors.size();
    }

    public void generateScheduleReport() {
        System.out.println("========== DOCTOR SCHEDULE REPORT ==========");
        System.out.println("Total Doctors: " + doctors.size());
        if (doctors.isEmpty()) {
            System.out.println("No doctors in the schedule.");
            return;
        }
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doc = doctors.get(i);
            System.out.println("\n" + doc.generateDoctorReport());
        }
        System.out.println("\n==========================================");
    }
}
