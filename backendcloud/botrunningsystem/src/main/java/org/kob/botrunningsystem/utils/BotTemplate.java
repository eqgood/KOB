package org.kob.botrunningsystem.utils;

public class BotTemplate {
    public static final String CODE =
            "package org.kob.botrunningsystem.utils;\n" +
            "import java.io.File;\n" +
                    "import java.io.FileNotFoundException;\n" +
                    "import java.util.ArrayList;\n" +
                    "import java.util.List;\n" +
                    "import java.util.Scanner;\n" +
                    "import java.util.function.Supplier;\n" +

                    "public class Bot implements Supplier<Integer> {\n" +

                    "    static class Cell{\n" +
                    "        public int x, y;\n" +
                    "        public Cell(int x, int y){\n" +
                    "            this.x = x;\n" +
                    "            this.y = y;\n" +
                    "        }\n" +
                    "    }\n" +

                    "    private boolean check_tail_increasing(int step){\n" +
                    "        if(step <= 10) return true;\n" +
                    "        return step % 3 == 1;\n" +
                    "    }\n" +

                    "    public List<Cell> getCells(int sx, int sy, String steps){\n" +
                    "        steps = steps.substring(1, steps.length()-1);\n" +
                    "        List<Cell> res = new ArrayList<>();\n" +
                    "        int[] dx = {-1,0,1,0}, dy = {0,1,0,-1};\n" +
                    "        int x = sx, y = sy;\n" +
                    "        res.add(new Cell(x,y));\n" +
                    "        int step=0;\n" +
                    "        for(int i=0;i<steps.length();i++){\n" +
                    "            int d = steps.charAt(i)-'0';\n" +
                    "            x += dx[d];\n" +
                    "            y += dy[d];\n" +
                    "            res.add(new Cell(x,y));\n" +
                    "            if(!check_tail_increasing(++step)) res.remove(0);\n" +
                    "        }\n" +
                    "        return res;\n" +
                    "    }\n" +

                    // ===== 用户写的完整 nextMove 函数放在这里 =====
                    "    {{USER_CODE}}\n" +
                    // =============================================

                    "    @Override\n" +
                    "    public Integer get() {\n" +
                    "        try {\n" +
                    "            Scanner sc = new Scanner(new File(\"input.txt\"));\n" +
                    "            String input = sc.next();\n" +
                    "            sc.close();\n" +

                    "            String[] strs = input.split(\"#\");\n" +
                    "            int[][] g = new int[13][14];\n" +
                    "            for(int i=0,k=0;i<13;i++)\n" +
                    "                for(int j=0;j<14;j++)\n" +
                    "                    if(strs[0].charAt(k++)=='1') g[i][j]=1;\n" +

                    "            int aSx = Integer.parseInt(strs[1]);\n" +
                    "            int aSy = Integer.parseInt(strs[2]);\n" +
                    "            int bSx = Integer.parseInt(strs[4]);\n" +
                    "            int bSy = Integer.parseInt(strs[5]);\n" +

                    "            List<Cell> aCells = getCells(aSx,aSy,strs[3]);\n" +
                    "            List<Cell> bCells = getCells(bSx,bSy,strs[6]);\n" +

                    "            for(Cell c:aCells) g[c.x][c.y]=1;\n" +
                    "            for(Cell c:bCells) g[c.x][c.y]=1;\n" +

                    "            int aHeadX = aCells.get(aCells.size()-1).x;\n" +
                    "            int aHeadY = aCells.get(aCells.size()-1).y;\n" +

                    "            return nextMove(g, aHeadX, aHeadY);\n" +
                    "        } catch (Exception e) {\n" +
                    "            return 0;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

    public static String fill(String userCode) {
        return CODE.replace("{{USER_CODE}}", userCode);
    }
}