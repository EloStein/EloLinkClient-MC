package elolink.de.client.util;

import elolink.de.client.errors.Error;
import elolink.de.client.errors.Errors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class ProfilePicture extends JButton {

    private BufferedImage image;

    public ProfilePicture(Path path) {
        try {
            image = ImageIO.read(new File(path.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(60, 60));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;

        int size = Math.min(getWidth(), getHeight());

        // create a circular clipping
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(image, 0, 0, size, size, null);

        g2.dispose();
    }

    public static void reformToPNGAndCreateFile(String base64, String uuid) {
        byte[] decoded = null;
        try {
            decoded = Base64.getDecoder().decode(base64);
        }catch (Exception e){
            Error.withCode(Errors.CouldNotGetPPBase64FromServer, "Base64: " + base64);
        }
        try {
            Files.write(Path.of(ResourceLocation.profilePicturesFolder + uuid + ".png"), decoded);
        } catch (IOException e) {}

        System.out.println("SAVED " + uuid);
    }

    public static String reformPngToBase64(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void actualizePP(String uuid){
        // TODO AAAAAAAAAAAa
        //reformToPNGAndCreateFile(HTTPConnection.getPPBase64(uuid),uuid);
    }

    public static void actualizeAllPP(){
        try {
            List list = FriendList.getFriendNames();
            for (Object s : list) {
                actualizePP(FriendList.getUuidFromName(s.toString()));
            }
        }catch (Exception e){}
    }

}
