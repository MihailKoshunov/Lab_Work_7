import java.net.*;
import java.util.*;
import java.io.*;


public class Crawler {

    static LinkedList <URLDepthPair> findLink = new LinkedList <URLDepthPair>();
    static LinkedList <URLDepthPair> viewedLink = new LinkedList <URLDepthPair>();


    public static void showResult(LinkedList<URLDepthPair> viewedLink) {
        for (URLDepthPair c : viewedLink)
            System.out.println("Depth : "+c.getDepth() + "\tLink : "+c.getURL());
    }


    public static void Process(String pair, int maxDepth) throws IOException
    {
        findLink.add(new URLDepthPair(pair,0));
        while (!findLink.isEmpty())
        {
            URLDepthPair current = findLink.removeFirst();
            if (current.depth <= maxDepth)
            {
                Socket socket = new Socket(current.getHost(),443);
                socket.setSoTimeout(1000);
                try {
                    URL address = new URL(current.getURL());
                    BufferedReader in = new BufferedReader(new InputStreamReader(address.openStream()));
                    String inputLine;
                    while((inputLine = in.readLine()) != null)
                    {
                        if (inputLine.contains("<a") && inputLine.contains("href="))
                        {
                            String linkToShow = inputLine.substring(inputLine.indexOf("href=")+6);
                            if ((linkToShow.startsWith("http://") || linkToShow.startsWith("https://")) && (!linkToShow.matches("^[а-яА-Я]")))
                            {
                                linkToShow = linkToShow.substring(0,linkToShow.indexOf('"'));
                                URLDepthPair catchPair = new URLDepthPair(linkToShow,current.depth+1);
                                if ((URLDepthPair.check(findLink,catchPair)) && (URLDepthPair.check(viewedLink,catchPair)))
                                {
                                    findLink.add(catchPair);
                                }
                            }
                        }
                    }
                    in.close();
                    viewedLink.add(current);
                }
                catch (IOException e)
                {
                    viewedLink.add(current);
                }
                socket.close();
            }
        }
    }

    public static void main(String[] args) {
        String httpPage = "https://slashdot.org/";
        int depth = 2;
        try {
            Process(httpPage, depth);
            showResult(viewedLink);
        } catch (NumberFormatException | IOException e) {
            showResult(viewedLink);
        }
    }
}
