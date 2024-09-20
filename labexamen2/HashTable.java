package labexamen2;

public class HashTable {

    Entry inicio;
    int contar = 0;

    public void add(String user, long pos) {
        if (Search(user) != -1) {
            return;
        }
        Entry nodo = new Entry(user, pos);
        if (isVacio()) {
            inicio = nodo;
        } else {
            Entry temp = inicio;
            while (temp.siguiente != null) {
                temp = temp.siguiente;
            }
            temp.siguiente = nodo;
        }
        contar++;
    }

    public void remove(String user) {
        if (!isVacio()) {
            if (inicio.username.equals(user)) {
                inicio = inicio.siguiente;
                contar--;
            } else {
                Entry temp = inicio;
                while (temp.siguiente != null) {
                    if (temp.username.equals(user)) {
                        temp.siguiente = temp.siguiente.siguiente;
                        contar--;
                        return;
                    } else {
                        temp = temp.siguiente;
                    }
                }
            }
        }
    }

    public boolean isVacio() {
        return inicio == null;
    }

    public long Search(String user) {
        if (!isVacio()) {
            Entry temp = inicio;
            while (temp != null) {
                if (temp.username.equals(user)) {
                    return temp.posicion;
                }
                temp = temp.siguiente;
            }
        }
        return -1;
    }

}
