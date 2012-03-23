package pe;

import jaxb.*;
import org.apache.commons.io.*;

import javax.xml.bind.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

public class Downloader {
    public static void main(String[] args) throws Exception {

        JAXBContext jc = JAXBContext.newInstance("jaxb");
        MxplayerPlayerlist mxplayerPlayerlist = new ObjectFactory().createMxplayerPlayerlist();

        mxplayerPlayerlist.setName("Playlist " + SimpleDateFormat.getDateTimeInstance().format(new Date()));
        mxplayerPlayerlist.setVer(BigInteger.valueOf(1));
        PlaylistItems playlistItems = new ObjectFactory().createPlaylistItems();
        mxplayerPlayerlist.setPlaylistItems(playlistItems);

        System.out.println("Starting to fetch pages");
        //  iterate over the pages //////////////
//        for (int i = 1; i <= 90; i++)

        boolean found = false;
        int i = 83;
        while (!found) {

            //  read contents of one page
            //http://www.livingelectro.com/All/1/?
            String url = "http://www.livingelectro.com/All/" + i++ + "/?";
            String contents = getUrlContent(url);

            System.out.println("querying " + url);

            // TODO extend match for a complete item, so the real name could be taken
            Pattern compile = Pattern.compile("http://streams[0-9]\\.tunescoop\\.com/.*?\\.flv", Pattern.DOTALL);
            //http://streams7.tunescoop.com/106475_Arty,_Matisse__Sadko_-_Trio_Original_Mix_www.livingelectro.com.flv
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

                if (songName.trim().contains("rundfunk")){
                    System.out.println("found at page: " + i);
                    found = true;
                }

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
