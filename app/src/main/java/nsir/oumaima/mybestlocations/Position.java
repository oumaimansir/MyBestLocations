package nsir.oumaima.mybestlocations;

public class Position {
    public int idposition;
    public String pseudo,numero,longitude,latitude;

    public Position(int idposition, String pseudo, String numero, String longitude, String latitude) {
        this.idposition = idposition;
        this.pseudo = pseudo;
        this.numero = numero;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Position{" +
                "idposition=" + idposition +
                ", pseudo='" + pseudo + '\'' +
                ", numero='" + numero + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    // Getter methods for latitude and longitude
    public int getIdposition() {
        return idposition;
    }
    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getNumero() {
        return numero;
    }
}
