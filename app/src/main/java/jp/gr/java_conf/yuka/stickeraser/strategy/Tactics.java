package jp.gr.java_conf.yuka.stickeraser.strategy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jp.gr.java_conf.yuka.stickeraser.manager.StickManager;
import jp.gr.java_conf.yuka.stickeraser.model.ErasePlace;
import jp.gr.java_conf.yuka.stickeraser.util.ArrayUtil;

public class Tactics {
    private static Random random = new Random();

    private Tactics() {
    }

    public static int[] getBlockSummary(List<StickManager.Block> blockList) {
        // サマリ情報
        // 最大のjoin数
        int maxJoin = 0;
        for (StickManager.Block block : blockList) {
            if (block.getJoinNum() > maxJoin) {
                maxJoin = block.getJoinNum();
            }
        }

        int[] blockSummary = new int[maxJoin];
        for (StickManager.Block block : blockList) {
            blockSummary[block.getJoinNum()-1]++;
        }

        return blockSummary;
    }

    public static List<Integer> getOddBlockList(int[] blockSummary) {
        List<Integer> oddBlockList = new ArrayList<Integer>();

        for (int i=0; i < blockSummary.length; i++) {
            if (blockSummary[i] % 2 == 1) {
                // 奇数ブロックの場合の本数
                oddBlockList.add(i+1);
            }
        }

        return oddBlockList;
    }

    public static StickManager.Block getRandomBlock(List<StickManager.Block> blockList) {
        // Blockの選択
        StickManager.Block targetBlock = blockList.get(random.nextInt(blockList.size()));

        // 始点の選択
        int start = random.nextInt(targetBlock.getJoinNum());

        // 終点の選択
        int numStick = Math.min((targetBlock.getJoinNum() - start), random.nextInt(3) + 1);

        StickManager.Block block = new StickManager.Block((targetBlock.getStartStickId() + start), numStick);

        return block;
    }

    public static StickManager.Block getBlockOfClosingErasePattern(List<StickManager.Block> blockList, int[] blockSummary) {

        ErasePlace erasePlace = Tactics.getClosingErasePattern(blockSummary);

        if (erasePlace == null) {
            return null;
        }

        Log.d("getEraseSticks", "closing pattern matched.[erasePlace=" + erasePlace.toString() + "]");

        return getBlock(erasePlace, blockList);
    }

    public static StickManager.Block getBlockOfEvenPattern(List<StickManager.Block> blockList, int[] blockSummary) {
        // 本数が同じブロックが奇数個の、本数
        List<Integer> oddBlocks = getOddBlockList(blockSummary);

        ErasePlace erasePlace = getEvenErasePattern(oddBlocks);

        if (erasePlace == null) {
            return null;
        }

        Log.d("getEraseSticks", "even pattern matched.[erasePlace=" + erasePlace.toString() + "]");

        return getBlock(erasePlace, blockList);

    }

    public static StickManager.Block getBlockOfAvoidingOpponentWinPattern (List<StickManager.Block> blockList, int[] blockSummary) {
        ErasePlace erasePlace = getAvoidingOpponentWinPattern(blockList, blockSummary);

        if (erasePlace == null) {
            return null;
        }

        return getBlock(erasePlace, blockList);

    }

    /**
     * 1本戦の塊が奇数個になるような消し方を探して返します。
     *
     * @param blockSummary
     * @return closingできる場合、erasePattern。できない場合null
     */
    private static ErasePlace getClosingErasePattern(int[] blockSummary) {
        // 1個しかない場合
        if (blockSummary.length == 1) {
            return null;
        }

        // 5より多い場合はreturn
        if  (blockSummary.length > 5) {
            return null;
        }

        // 最大長の塊の数が 1 でない場合はreturn
        if (blockSummary[blockSummary.length - 1] != 1) {
            return null;
        }

        // 2以上で棒が存在する最小のindexを探す
        int minIdx = 1;
        while (blockSummary[minIdx] == 0) {
            minIdx ++;
        }

        // 最小 + 1 == blockSummary.length でない場合はreturn
        if (minIdx + 1 != blockSummary.length) {

            return null ;
        }

        if (blockSummary[0] % 2 == 1) {
            // 1の数が奇数
            // 0または2本残して消す

            if (blockSummary.length == 5 || blockSummary.length == 4) {
                // 5か4の場合は2本残し
                return new ErasePlace(blockSummary.length, 1, blockSummary.length-2);
            } else if (blockSummary.length == 3) {
                // 3の場合は0または2本残し
                if (random.nextInt(2) < 1) {
                    return new ErasePlace(blockSummary.length, 1, blockSummary.length-2);
                } else {
                    return new ErasePlace(blockSummary.length, 0, blockSummary.length);
                }
            } else {
                // 2の場合は全部消し
                return new ErasePlace(blockSummary.length, 0, blockSummary.length);
            }
        } else {
            // 1の数が偶数

            // 5の場合は不可
            if (blockSummary.length == 5) {
                return null;
            }

            // 1本残して消す
            if (random.nextInt(2) < 1) {
                return new ErasePlace(blockSummary.length, 0, blockSummary.length-1);
            } else {
                return new ErasePlace(blockSummary.length, 1, blockSummary.length-1);
            }
        }

    }

    /**
     * oddBlocksをなくすようなerasePatternを返します
     * @param oddBlocks
     * @return oddBlocksをなくすことができる場合、oddBlocksをなくすようなerasePattern。なくすことができない場合はnull
     */
    private static ErasePlace getEvenErasePattern(List<Integer> oddBlocks) {

        if (oddBlocks.size() > 3) {
            // 奇数の個数が4つ以上のときはoddBlockにできない
            return null;
        }

        // 本数が同じブロックをすべて偶数個にできるか
        if (oddBlocks.size() == 1) {
            int joinNum = oddBlocks.get(0);
            if(joinNum % 2 == 1) {
                // blockに含まれる本数が奇数
                if (joinNum == 1 || random.nextInt(2) < 1) {
                    // 1本消す
                    return new ErasePlace(joinNum, joinNum/2, 1);
                } else {
                    // 3本消す
                    return new ErasePlace(joinNum, joinNum/2 - 1, 3);
                }

            } else {
                // blockに含まれる本数が偶数
                // 2本消す
                return new ErasePlace(joinNum, joinNum/2 - 1, 2);
            }

        } else if (oddBlocks.size() == 2) {
            int max = Math.max(oddBlocks.get(0), oddBlocks.get(1));
            int min = Math.min(oddBlocks.get(0), oddBlocks.get(1));
            int diff = max - min;

            if (diff <= 3) {
                if (random.nextInt(2) < 1) {
                    return new ErasePlace(max, 0, diff);
                } else {
                    return new ErasePlace(max, max - diff, diff);
                }
            }

        } else if (oddBlocks.size() == 3) {
            // 整列
            Collections.sort(oddBlocks);

            int min1 = oddBlocks.get(0);
            int min2 = oddBlocks.get(1);
            int max = oddBlocks.get(2);

            int diff = max - (min1 + min2);

            if (1 <= diff && diff <= 3) {
                if (random.nextInt(2) < 1) {
                    // min1 + diff + min2 に分割
                    return new ErasePlace(max, min1, diff);
                } else {
                    // min2 + diff + min1 に分割
                    return new ErasePlace(max, min2, diff);
                }
            }

        }

        return null;
    }

    private static ErasePlace getAvoidingOpponentWinPattern(List<StickManager.Block> blockList, int[] blockSummary) {
        List<Integer> randomAccessArray = ArrayUtil.getRandomAccessIntArray(blockSummary.length);

        // 本数が同じブロックが奇数個の、本数
        List<Integer> oddBlocks = getOddBlockList(blockSummary);
        // 手評価
        int currentScore = 0;

        ErasePlace erasePlace = null;

        // すべての手に対して
        for (int i = 0; i< blockSummary.length; i++) {
            int targetStickNum = randomAccessArray.get(i) + 1;
            if (blockSummary[targetStickNum -1] == 0) {
                continue;
            }

            // numStickがiであるblockをランダムに探す
            StickManager.Block block = getTargetBlock(targetStickNum, blockList);
            for (int offset = 0; offset < (block.getJoinNum() - 1) / 2; offset++) {
                int maxStickNum = Math.min(3, block.getJoinNum() - offset);
                for (int stickNum = 1; stickNum <= maxStickNum; stickNum++) {
                    int tmpScore = 0;

                    ErasePlace tmpErasePlace = new ErasePlace(block.getJoinNum(), offset, stickNum);
                    Log.v("blockSummary", "tmpErasePlace" + tmpErasePlace);

                    // 消した時のblockSummary
                    int[] newBlockSummary = getNewBlockSummary(blockSummary, tmpErasePlace);
                    Log.v("blockSummary", "oldBlockSummary" + Arrays.toString(blockSummary));
                    Log.v("blockSummary", "newBlockSummary" + Arrays.toString(newBlockSummary));

                    // 消した時のoddBlocks
                    List<Integer> newOddBlocks = getNewOddBlocks(oddBlocks, tmpErasePlace);
                    Log.v("blockSummary", "oldOddBlocks" + oddBlocks.toString());
                    Log.v("blockSummary", "newOddBlocks" + newOddBlocks.toString());

                    if (getClosingErasePattern(newBlockSummary) == null) {
                        // 次でclosingできない
                        tmpScore += 4;
                    }

                    if (getEvenErasePattern(newOddBlocks) == null) {
                        // 偶数パターンに持ち込めない
                        tmpScore += 2;
                    }

                    if (getWellKnownErasePattern(newBlockSummary, newOddBlocks) == null) {
                        tmpScore += 1;
                    }

                    if (tmpScore >= currentScore) {
                        if (tmpScore == currentScore && random.nextInt(2) < 1) {
                            continue;
                        }

                        currentScore = tmpScore;
                        // 対称性を考慮してrandomにする
                        if (random.nextInt(2) < 1) {
                            erasePlace = tmpErasePlace;
                        } else {
                            int newOffset = tmpErasePlace.getTargetBlockNum() - tmpErasePlace.getNumStick() - tmpErasePlace.getOffset();
                            erasePlace =  new ErasePlace(tmpErasePlace.getTargetBlockNum(), newOffset,  tmpErasePlace.getNumStick());
                        }
                    }
                }
            }
        }

        return erasePlace;
    }


    private static ErasePlace getWellKnownErasePattern(int[] blockSummary,
                                                List<Integer> oddBlocks) {

        // 1-2-3パターン
        if (blockSummary[0] == 0 ||  blockSummary[1] == 0 || blockSummary[2] == 0) {
            return null;
        }

        // oddBlocksをコピー
        List<Integer> tmpOddBlocks = new ArrayList<Integer>(oddBlocks);

        if (tmpOddBlocks.contains(Integer.valueOf(1))) {
            tmpOddBlocks.remove(Integer.valueOf(1));
        } else {
            tmpOddBlocks.add(Integer.valueOf(1));
        }

        if (tmpOddBlocks.contains(Integer.valueOf(2))) {
            tmpOddBlocks.remove(Integer.valueOf(2));
        } else {
            tmpOddBlocks.add(Integer.valueOf(2));
        }

        if (tmpOddBlocks.contains(Integer.valueOf(3))) {
            tmpOddBlocks.remove(Integer.valueOf(3));
        } else {
            tmpOddBlocks.add(Integer.valueOf(3));
        }

        ErasePlace erasePlace = getEvenErasePattern(tmpOddBlocks);

        if (erasePlace != null && blockSummary[erasePlace.getTargetBlockNum() - 1] != 0) {
            return erasePlace;
        }

        return null;
    }

    /**
     * erasePlaceで削除したときの新しいblockSummeryを返します
     * @param blockSummary 古いblockSummary
     * @param erasePlace
     * @return erasePlaceで削除したときのblockSummery
     */
    private static int[] getNewBlockSummary(int[] blockSummary,
                                     ErasePlace erasePlace) {
        // 古い配列をコピー
        int[] newBlockSummary = Arrays.copyOf(blockSummary, blockSummary.length);

        // 削除した後の左右の数
        int rightNum = erasePlace.getOffset();
        int leftNum = erasePlace.getTargetBlockNum() - erasePlace.getOffset() - erasePlace.getNumStick();

        //更新
        newBlockSummary[erasePlace.getTargetBlockNum() - 1]--;

        if (rightNum > 0) {
            newBlockSummary[rightNum - 1]++;
        }
        if (leftNum > 0) {
            newBlockSummary[leftNum - 1]++;
        }
        return newBlockSummary;
    }

    /**
     * erasePlaceで削除したときの新しいoddBlocksを返します
     * @param oddBlocks 古いoddBlocks
     * @param erasePlace
     * @return erasePlaceで削除したときのoddBlocks
     */
    private static List<Integer> getNewOddBlocks(List<Integer> oddBlocks,
                                          ErasePlace erasePlace) {
        // 古いListをコピー
        List<Integer> newOddBlocks = new ArrayList<Integer>(oddBlocks);

        // 削除した後の左右の数
        Integer rightNum = erasePlace.getOffset();
        Integer leftNum = erasePlace.getTargetBlockNum() - erasePlace.getOffset() - erasePlace.getNumStick();

        if (newOddBlocks.contains(erasePlace.getTargetBlockNum())) {
            newOddBlocks.remove(Integer.valueOf(erasePlace.getTargetBlockNum()));
        } else {
            newOddBlocks.add(Integer.valueOf(erasePlace.getTargetBlockNum()));
        }

        if (rightNum > 0) {
            if (newOddBlocks.contains(rightNum)) {
                newOddBlocks.remove(rightNum);
            } else {
                newOddBlocks.add(rightNum);
            }
        }
        if (leftNum > 0) {
            if (newOddBlocks.contains(leftNum)) {
                newOddBlocks.remove(leftNum);
            } else {
                newOddBlocks.add(leftNum);
            }
        }

        return newOddBlocks;
    }


    private static StickManager.Block getBlock(ErasePlace erasePlace, List<StickManager.Block> blockList) {
        List<Integer> randomAccessArray = ArrayUtil.getRandomAccessIntArray(blockList.size());

        StickManager.Block targetBlock = null;
        for (int i = 0; i< blockList.size(); i++) {
            StickManager.Block block = blockList.get(randomAccessArray.get(i));
            if (erasePlace.getTargetBlockNum() == block.getJoinNum()) {
                targetBlock = block;
                break;
            }
        }

        return new StickManager.Block((targetBlock.getStartStickId() + erasePlace.getOffset()), erasePlace.getNumStick());
    }

    private static StickManager.Block getTargetBlock(int numSticks, List<StickManager.Block> blockList) {
        List<Integer> randomAccessArray = ArrayUtil.getRandomAccessIntArray(blockList.size());

        StickManager.Block targetBlock = null;
        for (int i = 0; i< blockList.size(); i++) {
            StickManager.Block block = blockList.get(randomAccessArray.get(i));
            if (numSticks == block.getJoinNum()) {
                targetBlock = block;
                break;
            }
        }

        return targetBlock;
    }
}
