package ncu.lib.util;

import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenli-han on 15/1/8.
 */
public class NewBooksXmlParser {
    public List parse(InputStream is) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, "UTF-8");
            parser.nextTag();

            return readFeed(parser);
        } finally {
            is.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        Entry entry = null;
        String text = "";

/*      parser.require(XmlPullParser.START_TAG, null, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.d("test", name);
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        } */

        int eventType = parser.getEventType();

        while(eventType != XmlPullParser.END_DOCUMENT) {
            String tagname = parser.getName();

            switch(eventType) {
                case XmlPullParser.START_TAG:
                    if(tagname.equalsIgnoreCase("item")) {
                        entry = new Entry();
                    } else if(tagname.equalsIgnoreCase("interval")) {
                        entry = new Entry();
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if(tagname.equalsIgnoreCase("title")) {
                        if(entry != null)
                            entry.title = text;
                    } else if(tagname.equalsIgnoreCase("description")) {
                        if(entry != null)
                            entry.description = text.replaceAll("\\<.*?\\>","").replaceAll("\\s+","");
                    } else if(tagname.equalsIgnoreCase("link")) {
                        if(entry != null)
                            entry.link = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
                    } else if(tagname.equalsIgnoreCase("interval")) {
                        if(entry != null) {
                            entry.title = text;
                            entry.link = "";
                            entry.description = "";
                        }
                        entries.add(entry);
                    } else if(tagname.equalsIgnoreCase("item")) {
                        entries.add(entry);
                    }
                    break;
                default:
                    break;
            }

            eventType = parser.next();
        }

        return entries;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");

        String title = null;
        String description = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
                Log.d("test", title);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(title, description, link);

    }

    private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, null, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return description;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        return link;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    public class Entry {
        public String title;
        public String link;
        public String description;

        private Entry(String title, String description, String link) {
            this.title = title;
            this.description = description;
            this.link = link;
        }

        public Entry() {
            this.title = "";
            this.description = "";
            this.link = "";
        }

    }

}
