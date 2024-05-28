package kss.merkle.model;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.security.InvalidParameterException;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MerkleTree {
    private MerkleNode root;
    private Integer size;

    public static MerkleTree fromList(List<String> items) {
        if (!isPowerOfTwo(items.size())) throw new InvalidParameterException("Incorrect number of items");

        return new MerkleTree(createNode(items), items.size());
    }

    private static MerkleNode createNode(List<String> items) {
        if (items.size() == 1) {
            return new MerkleLeaf(items.stream().findFirst().get());
        } else {
            var divided = Lists.partition(items, items.size() / 2);
            var left = createNode(divided.getFirst());
            var right = createNode(divided.getLast());

            return new MerkleNode(left, right);
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    @Override
    public String toString() {
        return String.format("MerkleTree{ size=%d rootHash=%s }\n",
                size, BaseEncoding.base16().lowerCase().encode(root.hash)) + root;
    }
}
