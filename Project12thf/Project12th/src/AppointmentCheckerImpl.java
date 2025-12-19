import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.Serializable;

public class AppointmentCheckerImpl implements AppointmentValidator, Serializable {
    
    @Override
    public boolean isAppointmentExists(Appointment newAppointment, ArrayList<Appointment> existingAppointments) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        for (Appointment existing : existingAppointments) {
            // Skip cancelled appointments
            if (existing.getStatus().equals("CANCELLED")) {
                continue;
            }
            
            // Check if same doctor has appointment at same time
            if (newAppointment.getDoctor().getId().equals(existing.getDoctor().getId())) {
                if (newAppointment.getDate().equals(existing.getDate())) {
                    System.out.println("Error: Dr. " + newAppointment.getDoctor().getName() + 
                                     " already has an appointment at " + dateFormat.format(newAppointment.getDate()));
                    return true;
                }
            }
            
            // Check if same patient has appointment at same time
            if (newAppointment.getPatient().getId().equals(existing.getPatient().getId())) {
                if (newAppointment.getDate().equals(existing.getDate())) {
                    System.out.println("Error: Patient " + newAppointment.getPatient().getName() + 
                                     " already has an appointment at " + dateFormat.format(newAppointment.getDate()));
                    return true;
                }
            }
        }
        
        return false;
    }
}
