import java.util.ArrayList;

public interface AppointmentValidator {

    boolean isAppointmentExists(Appointment newAppointment, ArrayList<Appointment> existingAppointments);
}
