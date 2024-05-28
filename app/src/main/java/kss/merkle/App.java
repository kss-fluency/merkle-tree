/*
 * This source file was generated by the Gradle 'init' task
 */
package kss.merkle;

import kss.merkle.model.MerkleTree;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        final var treeSize = 32;
        Random rand = new Random();
        List<String> randomHexStrings = IntStream.generate(() -> rand.nextInt(Integer.MAX_VALUE)).limit(treeSize)
                .mapToObj(i -> String.format("0x%08x", i)).toList();

        MerkleTree tree = MerkleTree.fromList(randomHexStrings);
        System.out.println(tree);
    }
}
