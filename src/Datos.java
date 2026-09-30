
public class Datos {
    public static String usuarioActual;
    public static String nombreFch;
    public static String archivoInicialFch;
    public static String envioEmailFch;
    public static String emailUsuario;
    public static String respUsuario;
    public static String carpetaFch;


    public void setUsuarioActual(String usuario){ usuarioActual = usuario;}
    public void setNombreFch(String nombre){nombreFch = nombre;}
    public void setEnvioEmailFch(String emailF) { envioEmailFch = emailF;}
    public void setEmailUsuario(String emailF) { emailUsuario = emailF;}
    public void setRespUsuario( String respF) {respUsuario = respF;}
    public void setArchivoInicialFch(String archIniF) {
        archivoInicialFch = archIniF;
        if (archIniF == null || archIniF.isEmpty()) { carpetaFch = ""; return;}
        int lastIndex = archIniF.lastIndexOf("/");
        if (lastIndex >= 0) { carpetaFch = archIniF.substring(0, lastIndex);}
        else { carpetaFch = "";}
    }

    public String getUsuarioActual(){ return usuarioActual;}
    public String getNombreFch(){ return nombreFch;}
    public String getEnvioEmailFch(){ return envioEmailFch;}
    public String getEmailUsuario(){ return emailUsuario;}
    public String getRespUsuario(){ return respUsuario;}
    public String getArchivoInicialFch(){ return archivoInicialFch;}
    public String getCarpetaFch(){ return carpetaFch;}



    public static void inicializar() {
        usuarioActual = "";
        nombreFch = "";
        archivoInicialFch = "";
        envioEmailFch = "";
        emailUsuario = "";
        respUsuario = "";
        carpetaFch = "";
    }

}
