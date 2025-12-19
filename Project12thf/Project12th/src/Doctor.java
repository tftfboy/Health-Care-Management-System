import java.util.ArrayList;

public class Doctor extends Person {
    private String specialization;
    private String licenseNumber;
    private int yearsOfExperience;
    private String availabilitySchedule;
    private ArrayList<String> availableDays; // Days of week: Monday, Tuesday, etc.
    private ArrayList<DateSlots> bookedSlots; // List of date and time slots
    
    // Helper class to store date and its booked times
    private static class DateSlots implements java.io.Serializable {
        String date;
        ArrayList<String> times;
        
        DateSlots(String date) {
            this.date = date;
            this.times = new ArrayList<>();
        }
    }

    public Doctor(String name, String address, String phone, String specialization, String licenseNumber, int yearsOfExperience) {
        super(IdGenerator.generateDoctorId(), name, address, phone);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.availabilitySchedule = "Not Set";
        this.availableDays = new ArrayList<>();
        this.bookedSlots = new ArrayList<>();
    }
    
    public ArrayList<String> getAvailableDays() {
        return availableDays;
    }

    public void addAvailableDay(String day) {
        if (!availableDays.contains(day)) {
            availableDays.add(day);
        }
    }
    
    public void removeAvailableDay(String day) {
        availableDays.remove(day);
    }
    
    public void clearAvailableDays() {
        availableDays.clear();
    }
    
    // Get dates for next 7 days that match the doctor's available days
    public ArrayList<String> getAvailableDates() {
        ArrayList<String> dates = new ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.format.DateTimeFormatter dayFormatter = java.time.format.DateTimeFormatter.ofPattern("EEEE");
        
        for (int i = 0; i < 7; i++) {
            java.time.LocalDate date = today.plusDays(i);
            String dayOfWeek = date.getDayOfWeek().toString();
            String dayName = date.format(dayFormatter);
            
            // Check if this day of week is in doctor's available days
            if (availableDays.contains(dayOfWeek)) {
                String dateStr = date.format(formatter);
                // Only add if not fully booked
                DateSlots dateSlot = findDateSlot(dateStr);
                if (dateSlot == null || dateSlot.times.size() < 9) {
                    dates.add(dayName + " - " + dateStr);
                }
            }
        }
        return dates;
    }
    
    private DateSlots findDateSlot(String date) {
        for (DateSlots slot : bookedSlots) {
            if (slot.date.equals(date)) {
                return slot;
            }
        }
        return null;
    }
    
    public void bookTimeSlot(String date, String time) {
        DateSlots dateSlot = findDateSlot(date);
        if (dateSlot == null) {
            dateSlot = new DateSlots(date);
            bookedSlots.add(dateSlot);
        }
        dateSlot.times.add(time);
    }
    
    public boolean isTimeSlotAvailable(String date, String time) {
        DateSlots dateSlot = findDateSlot(date);
        if (dateSlot == null) {
            return true;
        }
        return !dateSlot.times.contains(time);
    }
    
    public ArrayList<String> getAvailableTimesForDate(String date) {
        ArrayList<String> allTimes = new ArrayList<>();
        allTimes.add("09:00 AM");
        allTimes.add("10:00 AM");
        allTimes.add("11:00 AM");
        allTimes.add("12:00 PM");
        allTimes.add("01:00 PM");
        allTimes.add("02:00 PM");
        allTimes.add("03:00 PM");
        allTimes.add("04:00 PM");
        allTimes.add("05:00 PM");
        
        DateSlots dateSlot = findDateSlot(date);
        if (dateSlot != null) {
            allTimes.removeAll(dateSlot.times);
        }
        
        return allTimes;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getAvailabilitySchedule() {
        return availabilitySchedule;
    }



    public boolean isAvailable(String day) {
        return availabilitySchedule.contains(day);
    }



    public String getDoctorDetails() {
        return "Doctor{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", availabilitySchedule='" + availabilitySchedule + '\'' +
                '}';
    }

    public boolean isExperienced() {
        return yearsOfExperience >= 5;
    }

    public String generateDoctorReport()
    {
        return "Doctor Report:\n" +
                "Name: " + getName() + "\n" +
                "Specialization: " + specialization + "\n" +
                "License Number: " + licenseNumber + "\n" +
                "Years of Experience: " + yearsOfExperience + "\n" +
                "Availability: " + availabilitySchedule;
    }
}
