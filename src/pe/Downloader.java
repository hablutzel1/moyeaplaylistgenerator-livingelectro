package pe;

import jaxb.*;
import org.apache.commons.io.*;

import javax.xml.bind.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.util.regex.*;

public class Downloader {
    public static void main(String[] args) throws Exception {

        JAXBContext jc = JAXBContext.newInstance("jaxb");
        MxplayerPlayerlist mxplayerPlayerlist = new ObjectFactory().createMxplayerPlayerlist();

        mxplayerPlayerlist.setName("MyPlaylist");
        mxplayerPlayerlist.setVer(BigInteger.valueOf(1));
        PlaylistItems playlistItems = new ObjectFactory().createPlaylistItems();
        mxplayerPlayerlist.setPlaylistItems(playlistItems);

        System.out.println("Starting to fetch pages");
        //  iterate over the pages //////////////
        for (int i = 0; i < 10; i++) {

            //  read contents of one page
            String contents = getUrlContent("http://www.livingelectro.com/index.php?previous=" + i);

            // TODO extend match for a complete item, so the real name could be taken
            Pattern compile = Pattern.compile("http://streams5\\.tunescoop\\.com/.*?\\.flv", Pattern.DOTALL);

            Matcher matcher = compile.matcher(contents);


            while (matcher.find()) {


                Item item = new ObjectFactory().createItem();
                item.setType(BigInteger.valueOf(2));
                String urlMatch = matcher.group();
                item.setVideoURL(urlMatch);


                Pattern pattern2 = Pattern.compile(".*/(.*)");
                Matcher matcher2 = pattern2.matcher(urlMatch);

                matcher2.find();
                String songName = matcher2.group(1);
                // TODO get real thumbnail

                item.setThumbnail(songName + ".jpg");
                item.setContent(songName);
                playlistItems.getItem().add(item);
                System.out.println(urlMatch);
            }

            System.out.println("Finished harvesting :)");

        }


        ///////

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("playlist.mmpl"));
        m.marshal(mxplayerPlayerlist, bufferedOutputStream);
        bufferedOutputStream.close();


    }

    private static String getUrlContent(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection huc = (HttpURLConnection) u.openConnection();
        huc.setRequestMethod("GET");
        huc.setDoOutput(true);
        huc.connect();
        InputStream inputStream = huc.getInputStream();
        return IOUtils.toString(inputStream);
    }
}
