package kss.merkle.model;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import kss.merkle.crypto.Sha256Hasher;
import kss.merkle.exception.MerkleException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MerkleTree {
    private MerkleNode root;
    private Integer size;

    public static MerkleTree fromList(List<String> items) throws MerkleException {
        if (!isPowerOfTwo(items.size())) {
            throw new MerkleException(String.format("Merkle Tree can only be built using n^2 items but you provided %d", items.size()));
        }

        return new MerkleTree(createNode(items, 0), items.size());
    }

    public boolean verifyProof(String item, List<MerkleProofItem> proof) {
        byte[] itemHash = Sha256Hasher.hash(item.getBytes());

        byte[] calculatedRootHash = recursiveGenerateProof(root, itemHash, proof);

        return Arrays.equals(calculatedRootHash, root.hash);
    }

    private byte[] recursiveGenerateProof(MerkleNode item, byte[] itemHash, List<MerkleProofItem> hashes) {
        byte[] result = null;

        if (item instanceof MerkleLeaf) {
            if (Arrays.equals(item.getHash(), itemHash)) {
                result = item.getHash();
            }
        } else {
            MerkleProofItem firstProofItem = hashes.removeFirst();

            if (firstProofItem instanceof MerkleProofItem.Left l) {
                result = Sha256Hasher.hash(Bytes.concat(l.getHash(), recursiveGenerateProof(item.getRight(), itemHash, hashes)));
            } else if (firstProofItem instanceof MerkleProofItem.Right r) {
                result = Sha256Hasher.hash(Bytes.concat(recursiveGenerateProof(item.getLeft(), itemHash, hashes), r.getHash()));
            }
        }

        return result;
    }

    private static MerkleNode createNode(List<String> items, Integer depth) {
        if (items.size() == 1) {
            return new MerkleLeaf(items.stream().findFirst().get(), depth);
        } else {
            var divided = Lists.partition(items, items.size() / 2);
            var left = createNode(divided.getFirst(), depth + 1);
            var right = createNode(divided.getLast(), depth + 1);

            return new MerkleNode(left, right, depth);
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return (n != 0) && ((n & (n - 1)) == 0);
    }

    @Override
    public String toString() {
        return String.format("MerkleTree{ size=[%d] rootHash=[%s] }\n",
                size, BaseEncoding.base16().lowerCase().encode(root.hash)) + root;
    }
}
