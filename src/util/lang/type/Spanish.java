package util.lang.type;

import kernel.Config;
import util.lang.AbstractLang;

/**
 * Created by Locos on 09/12/2015.
 */
public class Spanish extends AbstractLang {

    public final static Spanish singleton = new Spanish();

    public static Spanish getInstance() {
        return singleton;
    }

    public void initialize() {
        int index = 0;
        this.sentences.add(index, "Tu canal global está desactivado."); index++;
        this.sentences.add(index, "Algunos carácteres usados en tu sentencia están deshabilitados."); index++;
        this.sentences.add(index, "Debes esperar #1 segundo(s)."); index++;
        this.sentences.add(index, "Se ha activado el canal general."); index++;
        this.sentences.add(index, "Se ha desactivado el canal general."); index++;
        this.sentences.add(index, "Lista del staff conectado :"); index++;
        this.sentences.add(index, "No hay ningún miembro del staff conectado."); index++;
        this.sentences.add(index, "No estás atascado..."); index++;
        this.sentences.add(index, "<b>" + Config.INSTANCE.getNAME() + "</b>\nOnline desde : #1j #2h #3m #4s."); index++;
        this.sentences.add(index, "\nJugadores online : #1"); index++;
        this.sentences.add(index, "\nJugadores únicos conectados : #1"); index++;
        this.sentences.add(index, "\nMayoría en línea : #1"); index++;
        this.sentences.add(index, "Los comandos disponibles son :\n"
                + "<b>.infos</b> - Da información del server.\n"
                + "<b>.deblo</b> - Te teletransporta a una celda libre.\n"
                + "<b>.staff</b> - Ver miembros del staff online.\n"
                + "<b>.all</b>   - Envía un mensaje a todos los jugadores.\n"
                + "<b>.noall</b> - No te permite recibir mensajes del canal General."); index++;
        this.sentences.add(index, "Ahora puedes votar, pulsando <b><a href='" + Config.INSTANCE.getUrl() + "'>aquí</a></b>.");index++;
        this.sentences.add(index, "You can't fight until new order."); index++;
        this.sentences.add(index, "The reboot has been stopped. Now, you can fight.");
    }
}
