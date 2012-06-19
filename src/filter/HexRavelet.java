package filter;

public interface HexRavelet {

    /**
	 * @author   Aleksejs Truhans
	 */
    public static enum Direction {
        //@formatter:off
        //          y0  x0  xdelta  ydelta
        ROW(        0,  1,  1,      0), 
        COLUMN(     1,  0,  0,      1), 
        DIAGONAL(   1,  1,  1,      -1);        
        // @formatter:on
    
        final int y0;
        final int x0;
        final int xdelta;
        final int ydelta;
    
        private Direction(int y0, int x0, int xdelta, int ydelta) {
            this.y0 = y0;
            this.x0 = x0;
            this.xdelta = xdelta;
            this.ydelta = ydelta;
        }
    }

    /**
	 * @author   Aleksejs Truhans
	 */
    public static enum Stage {
        //@formatter:off
        //      CenterCoef
        ENCODE (-1), 
        DECODE (1);
        //@formatter:on
    
        final int averageCoef;
    
        private Stage(int averageCoef) {
            this.averageCoef = averageCoef;
        }
    }

    final int HEXAGON_TRAVERSAL_XY[][] = { { 0, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 },
    { -1, 0 }, { -1, 1 } } ;
    
    void encode(); 
    void encodeLevel(int level);
    void decode(); 
    void decodeLevel(int level);

}
