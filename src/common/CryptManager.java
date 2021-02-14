package common;

import area.map.GameCase;
import area.map.GameMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class CryptManager {

    public final static char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '-', '_'};

    private final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public String cellID_To_Code(int cellID) {

        int char1 = cellID / 64, char2 = cellID % 64;
        return HASH[char1] + "" + HASH[char2];
    }

    public int cellCode_To_ID(String cellCode) {

        char char1 = cellCode.charAt(0), char2 = cellCode.charAt(1);
        int code1 = 0, code2 = 0, a = 0;

        while (a < HASH.length) {
            if (HASH[a] == char1)
                code1 = a * 64;
            if (HASH[a] == char2)
                code2 = a;
            a++;
        }
        return (code1 + code2);
    }

    public int getIntByHashedValue(char c) {
        for (int a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
    }

    public char getHashedValueByInt(int c) {
        return HASH[c];
    }

    public ArrayList<GameCase> parseStartCell(GameMap map, int num) {
        ArrayList<GameCase> list = null;
        String infos;
        if (!map.getPlaces().equalsIgnoreCase("-1")) {
            infos = map.getPlaces().split("\\|")[num];
            int a = 0;
            list = new ArrayList<>();
            while (a < infos.length()) {
                list.add(map.getCase((getIntByHashedValue(infos.charAt(a)) << 6)
                        + getIntByHashedValue(infos.charAt(a + 1))));
                a = a + 2;
            }
        }
        return list;
    }

    public List<GameCase> decompileMapData(GameMap map, String data, byte sniffed) {
        List<GameCase> cells = new ArrayList<>();
        int a = 0;
        for (int f = 0; f < data.length(); f += 10) {
            String mapData = data.substring(f, f + 10);
            List<Byte> cellInfos = new ArrayList<>();

            for (int i = 0; i < mapData.length(); i++)
                cellInfos.add((byte) getIntByHashedValue(mapData.charAt(i)));

            int walkable = ((cellInfos.get(2) & 56) >> 3);
            boolean los = (cellInfos.get(0) & 1) != 0;

            int layerObject2 = ((cellInfos.get(0) & 2) << 12) + ((cellInfos.get(7) & 1) << 12) + (cellInfos.get(8) << 6) + cellInfos.get(9);
            boolean layerObject2Interactive = ((cellInfos.get(7) & 2) >> 1) != 0;
            int object = (layerObject2Interactive && sniffed == 0 ? layerObject2 : -1);
            if((walkable != 0 && !mapData.equalsIgnoreCase("bhGaeaaaaa") && !mapData.equalsIgnoreCase("Hhaaeaaaaa")))
                a++;
            cells.add(new GameCase(map, (short) (f / 10), (walkable != 0 && !mapData.equalsIgnoreCase("bhGaeaaaaa") && !mapData.equalsIgnoreCase("Hhaaeaaaaa")), los, object));
        }
        return cells;
    }


    // prepareData
    public String cryptMessage(String message, String key) {
        StringBuilder str = new StringBuilder();
        // Append keyId
        str.append(HEX_CHARS[1]);
        // Append checksum
        int checksum = checksum(message);
        str.append(HEX_CHARS[checksum]);
        // Prepare key cause it's hexa form
        int c = checksum * 2;
        String data = encode(message);
        int keyLength = key.length();

        for (int i = 0; i < data.length(); i++)
            str.append(decimalToHexadecimal(data.charAt(i) ^ key.charAt((i + c) % keyLength)));

        return str.toString();
    }

    public String decryptMessage(String message, String key) {
        int c = Integer.parseInt(Character.toString(message.charAt(1)), 16) * 2;
        StringBuilder str = new StringBuilder();
        int j = 0, keyLength = key.length();

        for(int i = 2; i < message.length(); i = i + 2)
            str.append((char) (Integer.parseInt(message.substring(i, i + 2), 16) ^ key.charAt((j++ + c) % keyLength)));

        try {
            String data = str.toString();
            data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            data = data.replaceAll("\\+", "%2B");
            return URLDecoder.decode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String prepareKey(String key) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < key.length(); i += 2)
            sb.append((char) Integer.parseInt(key.substring(i, i + 2), 16));

        try {
            return URLDecoder.decode(sb.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private int checksum(String data) {
        int result = 0;
        for(char c : data.toCharArray())
            result += c % 16;
        return result % 16;
    }

    private String decimalToHexadecimal(int c) {
        if(c > 255) c = 255;
        return HEX_CHARS[c / 16] + "" + HEX_CHARS[c % 16];
    }

    private String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private boolean isUnsafe(char ch) {
        return ch > 255 || "+%".indexOf(ch) >= 0;
    }
}
