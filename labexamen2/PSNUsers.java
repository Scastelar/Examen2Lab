package labexamen2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class PSNUsers {

    RandomAccessFile raf;
    RandomAccessFile users;
    HashTable user;
    int contar = 0;

    public PSNUsers() {
        try {
            File usersFile = new File("users");
            if (!usersFile.exists()) {
                usersFile.createNewFile();
            }

            File psnFile = new File("psn");
            if (!psnFile.exists()) {
                psnFile.createNewFile();
            }

            users = new RandomAccessFile(usersFile, "rw");
            raf = new RandomAccessFile(psnFile, "rw");

        } catch (IOException ex) {
            System.out.print("Error al crear o abrir los archivos: " + ex.getMessage());
        }
        reloadHashTable();
    }

    private void reloadHashTable() {
        try {
            contar = 0;
            users.seek(contar);
            user = new HashTable();
            while (users.getFilePointer() < users.length()) {
                String usuario = users.readUTF();
                int puntos = users.readInt();
                int trofeos = users.readInt();
                boolean estado = users.readBoolean();

                if (!estado) {
                    System.out.println("Usuario eliminado.");
                }
                user.add(usuario, contar);
                contar++;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addUser(String username) {
        if (user.Search(username) != -1) {
            return false;
        }

        try {
            users.seek(users.length());
            users.writeUTF(username);
            users.writeInt(0);
            users.writeInt(0);
            users.writeBoolean(true);
            user.add(username, contar);
            contar++;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void deactivateUser(String username) throws FileNotFoundException, IOException {
        if (user.Search(username) == -1) {
            return;
        }

        try {
            File temp = new File("tempUsers");
            temp.createNewFile();
            try (RandomAccessFile updateUsers = new RandomAccessFile(temp, "rw")) {
                users.seek(0);
                while (users.getFilePointer() < users.length()) {
                    String currentUsername = users.readUTF();
                    if (username.equals(currentUsername)) {
                        users.skipBytes(9);
                        continue;
                    }
                    int puntos = users.readInt();
                    int trofeos = users.readInt();
                    boolean estado = users.readBoolean();

                    updateUsers.writeUTF(currentUsername);
                    updateUsers.writeInt(puntos);
                    updateUsers.writeInt(trofeos);
                    updateUsers.writeBoolean(estado);
                }
            }
            users.close();
            File usersFile = new File("users");
            usersFile.delete();
            temp.renameTo(usersFile);
            users = new RandomAccessFile("users", "rw");
            reloadHashTable();

            File tempTrof = new File("temp");
            tempTrof.createNewFile();
            try (RandomAccessFile tempRaf = new RandomAccessFile("temp", "rw")) {
                raf.seek(0);
                while (raf.getFilePointer() < raf.length()) {
                    String user = raf.readUTF();
                    String trofeoTipo = raf.readUTF();
                    String juego = raf.readUTF();
                    String trofeoNombre = raf.readUTF();
                    long date = raf.readLong();

                    if (user.equals(username)) {
                        continue;
                    }
                    tempRaf.writeUTF(user);
                    tempRaf.writeUTF(trofeoTipo);
                    tempRaf.writeUTF(juego);
                    tempRaf.writeUTF(trofeoNombre);
                    tempRaf.writeLong(date);
                }
            }
            raf.close();

            File psnFile = new File("psn");
            psnFile.delete();
            tempTrof.renameTo(psnFile);
            raf = new RandomAccessFile("psn", "rw");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addTrophieTo(String username, String trophyGame, String trophyName, Trophy trophyType) {
        if (user.Search(username) == -1) {
            return false;
        }

        try {
            raf.seek(raf.length());
            raf.writeUTF(username);
            raf.writeUTF(trophyType.name());
            raf.writeUTF(trophyGame);
            raf.writeUTF(trophyName);

            Date currentDate = new Date();
            raf.writeLong(currentDate.getTime());

            users.seek(0);
            long currentPos;
            while (users.getFilePointer() < users.length()) {
                currentPos = users.getFilePointer();
                String UsuarioActual = users.readUTF();

                if (!UsuarioActual.equals(username)) {
                    users.skipBytes(9);
                    continue;
                }
                int puntos = users.readInt();
                int cant = users.readInt();
                boolean estado = users.readBoolean();

                if (!estado) {
                    return false;
                }
                puntos += trophyType.getPuntos();
                cant++;
                users.seek(currentPos);
                users.writeUTF(UsuarioActual);
                users.writeInt(puntos);
                users.writeInt(cant);
                users.writeBoolean(true);
                users.close();
                users = new RandomAccessFile("users", "rw");
                return true;
            }

            return false;

        } catch (IOException e) {
            return false;
        }
    }

    public String playerInfo(String username) {
        if (user.Search(username) == -1) {
            return "";
        }

        try {
            String texto = "";
            users.seek(0);
            while (users.getFilePointer() < users.length()) {
                String nombreactual = users.readUTF();
                int puntos = users.readInt();
                int cant = users.readInt();
                boolean estado = users.readBoolean();

                if (!nombreactual.equals(username) || !estado) {
                    continue;
                }
                if (nombreactual.equals(username) && estado == false) {
                    return "Usuario eliminado";
                }

                texto = "  Usuario: " + username + "\t      Puntos: " + puntos + "\t   Cantidad de trofeos: " + cant
                        + texto + "\n\n\t            Lista de Trofeos:\n";

                raf.seek(0);
                while (raf.getFilePointer() < raf.length()) {
                    String name = raf.readUTF();
                    String tipo = raf.readUTF();
                    String juego = raf.readUTF();
                    String nombre = raf.readUTF();
                    long date = raf.readLong();

                    if (name.equals(username)) {
                        texto += "\n - [" + new Date(date) + "]  Tipo: " + tipo + "  Juego: " + juego + "  Descripcion: " + nombre + "\n";
                        /*
                        texto += "\n - Trofeo " + trophyType + " de " + trophyGame + " llamado " + trophyName + " el ["
                                + new Date(date) + "]\n";
                                */
                    }
                }
                texto += "\n\n";
            }
            return texto;

        } catch (IOException e) {
            return "";
        }
    }
}
