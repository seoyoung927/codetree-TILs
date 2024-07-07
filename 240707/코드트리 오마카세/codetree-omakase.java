import java.util.*;
import java.io.*;

public class Main {
    static final int MAX_NAME = 15055;

    static int L; // L: 초밥 벨트의 길이
    static int Q; // Q: 명령의 수
    static Map<String, Integer> address;
    static int addIdx;
    static Map<Integer, Integer> sushiMap; // sushi 배열 대신 사용할 HashMap
    static int[] customer;
    static int[] remainS;
    static int[] remainC;
    static int prevT;

    static class Node {
        int idx;
        int cnt;
        public Node() {}
        public Node(int idx, int cnt) {
            this.idx = idx;
            this.cnt = cnt;
        }
    }

    // 시각 t에 위치 x의 고정 위치 return
    public static int getFixedPos(int t, int x) {
        int ret = x - t;
        if (ret < 0) {
            int tmp = ret * -1;
            int ans = tmp / 5;
            ret += (ans + 1) * L;
        }
        ret %= L;
        return ret;
    }

    // 시각 t의 고정 위치 x의 동적 위치 return
    public static int getDynamicPos(int t, int x) {
        int ret = x + t;
        ret %= L;
        return ret;
    }

    public static void main(String[] args) throws IOException {
        // 1. 입력 및 초기화
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        address = new HashMap<>();
        addIdx = 0;

        sushiMap = new HashMap<>();

        customer = new int[MAX_NAME];
        remainS = new int[MAX_NAME];
        remainC = new int[MAX_NAME];
        prevT = 0;

        for (int c = 0; c < Q; c++) {
            st = new StringTokenizer(br.readLine());
            int cmd = Integer.parseInt(st.nextToken());

            if (cmd == 100) {
                // 주방장의 초밥 만들기
                // 100 t x name
                int t = Integer.parseInt(st.nextToken());
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();

                if (!address.containsKey(name)) address.put(name, addIdx++);

                for (int time = prevT; time < Math.min(prevT+L, t); time++) {
                    for (int i = 0; i < addIdx; i++) {
                        if (remainS[i] == 0 || remainC[i] == 0) continue;

                        // 0~L까지 위치 계산
                        for (int j = 0; j < L; j++) {
                            int key = i * L + j;
                            if (!sushiMap.containsKey(key) || sushiMap.get(key) == 0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            if (dynamicPos == customer[i]) {
                                int sushiCount = sushiMap.get(key);
                                if (sushiCount >= remainC[i]) {
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, sushiCount - remainC[i]);
                                    remainC[i] = 0;
                                } else if (remainC[i] >= sushiCount) {
                                    remainC[i] -= sushiCount;
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, 0);
                                }
                            }
                        }
                    }
                }
                prevT = t - 1;

                int idx = address.get(name);
                int pos = getFixedPos(t, x);
                int key = idx * L + pos;
                sushiMap.put(key, sushiMap.getOrDefault(key, 0) + 1);
                remainS[idx]++;

            } else if (cmd == 200) {
                // 손님 입장
                // 200 t x name n
                int t = Integer.parseInt(st.nextToken());
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                int n = Integer.parseInt(st.nextToken());

                if (!address.containsKey(name)) address.put(name, addIdx++);

                for (int time = prevT; time < Math.min(prevT+L, t); time++) {
                    for (int i = 0; i < addIdx; i++) {
                        if (remainS[i] == 0 || remainC[i] == 0) continue;

                        // 0~L까지 위치 계산
                        for (int j = 0; j < L; j++) {
                            int key = i * L + j;
                            if (!sushiMap.containsKey(key) || sushiMap.get(key) == 0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            if (dynamicPos == customer[i]) {
                                int sushiCount = sushiMap.get(key);
                                if (sushiCount >= remainC[i]) {
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, sushiCount - remainC[i]);
                                    remainC[i] = 0;
                                } else if (remainC[i] >= sushiCount) {
                                    remainC[i] -= sushiCount;
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, 0);
                                }
                            }
                        }
                    }
                }
                prevT = t - 1;

                int idx = address.get(name);
                customer[idx] = x;
                remainC[idx] = n;

            } else if (cmd == 300) {
                // 사진 촬영
                // 300 t
                int t = Integer.parseInt(st.nextToken());

                for (int time = prevT; time <= Math.min(prevT+L, t); time++) {
                    for (int i = 0; i < addIdx; i++) {
                        if (remainS[i] == 0 || remainC[i] == 0) continue;

                        // 0~L까지 위치 계산
                        for (int j = 0; j < L; j++) {
                            int key = i * L + j;
                            if (!sushiMap.containsKey(key) || sushiMap.get(key) == 0) continue;

                            int dynamicPos = getDynamicPos(time, j);
                            if (dynamicPos == customer[i]) {
                                int sushiCount = sushiMap.get(key);
                                if (sushiCount >= remainC[i]) {
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, sushiCount - remainC[i]);
                                    remainC[i] = 0;
                                } else if (remainC[i] >= sushiCount) {
                                    remainC[i] -= sushiCount;
                                    remainS[i] -= sushiCount;
                                    sushiMap.put(key, 0);
                                }
                            }
                        }
                    }
                }
                prevT = t;

                int sushiCnt = 0;
                int customerCnt = 0;
                for (int i = 0; i < addIdx; i++) {
                    if (remainS[i] > 0) sushiCnt += remainS[i];
                    if (remainC[i] > 0) customerCnt++;
                }
                System.out.println(customerCnt + " " + sushiCnt);
            }
        }
    }
}