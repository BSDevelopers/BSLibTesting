package lib.brainsynder.utils;

public class Base64Wrapper {
    private static char[] map1 = new char[64];
    private static byte[] map2;

    public static String encodeString(String s) {
        byte[] bytes = s.getBytes();
        return new String(encode(bytes, 0, bytes.length));
    }

    public static String decodeString(String s) {
        char[] chars = s.toCharArray();
        return new String(decode(chars, 0, chars.length));
    }
    
    private static char[] encode(byte[] in, int iOff, int iLen) {
        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = (iLen + 2) / 3 * 4;
        char[] out = new char[oLen];
        int ip = iOff;
        int iEnd = iOff + iLen;

        for(int op = 0; ip < iEnd; ++op) {
            int i0 = in[ip++] & 255;
            int i1 = ip < iEnd?in[ip++] & 255:0;
            int i2 = ip < iEnd?in[ip++] & 255:0;
            int o0 = i0 >>> 2;
            int o1 = (i0 & 3) << 4 | i1 >>> 4;
            int o2 = (i1 & 15) << 2 | i2 >>> 6;
            int o3 = i2 & 63;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen?map1[o2]:61;
            ++op;
            out[op] = op < oDataLen?map1[o3]:61;
        }

        return out;
    }

    private static byte[] decode(char[] in, int iOff, int iLen) {
        if(iLen % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        } else {
            while(iLen > 0 && in[iOff + iLen - 1] == 61) {
                --iLen;
            }

            int oLen = iLen * 3 / 4;
            byte[] out = new byte[oLen];
            int ip = iOff;
            int iEnd = iOff + iLen;
            int op = 0;

            while(ip < iEnd) {
                char i0 = in[ip++];
                char i1 = in[ip++];
                char i2 = ip < iEnd?in[ip++]:65;
                char i3 = ip < iEnd?in[ip++]:65;
                if(i0 <= 127 && i1 <= 127 && i2 <= 127 && i3 <= 127) {
                    byte b0 = map2[i0];
                    byte b1 = map2[i1];
                    byte b2 = map2[i2];
                    byte b3 = map2[i3];
                    if(b0 >= 0 && b1 >= 0 && b2 >= 0 && b3 >= 0) {
                        int o0 = b0 << 2 | b1 >>> 4;
                        int o1 = (b1 & 15) << 4 | b2 >>> 2;
                        int o2 = (b2 & 3) << 6 | b3;
                        out[op++] = (byte)o0;
                        if(op < oLen) {
                            out[op++] = (byte)o1;
                        }

                        if(op < oLen) {
                            out[op++] = (byte)o2;
                        }
                        continue;
                    }

                    throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
                }

                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }

            return out;
        }
    }

    static {
        int i = 0;

        char c;
        for(c = 65; c <= 90; map1[i++] = c++) {
        }

        for(c = 97; c <= 122; map1[i++] = c++) {
        }

        for(c = 48; c <= 57; map1[i++] = c++) {
        }

        map1[i++] = 43;
        map1[i++] = 47;
        map2 = new byte[128];

        for(i = 0; i < map2.length; ++i) {
            map2[i] = -1;
        }

        for(i = 0; i < 64; ++i) {
            map2[map1[i]] = (byte)i;
        }

    }
}
