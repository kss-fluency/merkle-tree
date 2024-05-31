package kss.merkle.model;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import kss.merkle.crypto.Hasher;
import kss.merkle.crypto.Sha256Hasher;
import kss.merkle.exception.MerkleException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MerkleTree {
    private final MerkleNode root;
    private final Integer size;
    private final static Hasher hasher = new Sha256Hasher();

    public static MerkleTree fromList(List<String> items) throws MerkleException {
        if (!isPowerOfTwo(items.size())) {
            throw new MerkleException(String.format("Merkle Tree can only be built using n^2 items but you provided %d", items.size()));
        }

        return new MerkleTree(createNode(items, 0), items.size());
    }

    public boolean verifyProof(String item, List<MerkleProofItem> proof) {
        byte[] itemHash = hasher.hash(item.getBytes());

        byte[] calculatedRootHash = recursiveVerifyProof(root, itemHash, proof);

        return Arrays.equals(calculatedRootHash, root.hash);
    }

    public List<MerkleProofItem> generateProof(String item) throws MerkleException {
        byte[] itemHash = hasher.hash(item.getBytes());

        Optional<List<MerkleProofItem>> proof = recursiveGenerateProof(root, itemHash, Collections.emptyList());
        if (proof.isEmpty()) {
            throw new MerkleException("Item not found in the tree");
        }

        return proof.get();
    }

    private Optional<List<MerkleProofItem>> recursiveGenerateProof(MerkleNode node, byte[] itemHash, List<MerkleProofItem> proof) {
        if (node instanceof MerkleLeaf) {
            if (Arrays.equals(node.getHash(), itemHash)) {
                return Optional.of(proof);
            } else {
                return Optional.empty();
            }
        } else {
            List<MerkleProofItem> leftProof = new ArrayList<>(proof);
            leftProof.addFirst(new MerkleProofItem.Right(node.getRight().hash));
            Optional<List<MerkleProofItem>> left = recursiveGenerateProof(node.getLeft(), itemHash, leftProof);

            List<MerkleProofItem> rightProof = new ArrayList<>(proof);
            rightProof.addFirst(new MerkleProofItem.Left(node.getLeft().hash));
            Optional<List<MerkleProofItem>> right = recursiveGenerateProof(node.getRight(), itemHash, rightProof);

            return left.isPresent() ? left : right;
        }
    }

    private byte[] recursiveVerifyProof(MerkleNode item, byte[] itemHash, List<MerkleProofItem> hashes) {
        byte[] result = null;

        if (item instanceof MerkleLeaf) {
            if (Arrays.equals(item.getHash(), itemHash)) {
                result = item.getHash();
            }
        } else {
            MerkleProofItem firstProofItem = hashes.removeFirst();

            if (firstProofItem instanceof MerkleProofItem.Left l) {
                result = hasher.hash(Bytes.concat(l.getHash(), recursiveVerifyProof(item.getRight(), itemHash, hashes)));
            } else if (firstProofItem instanceof MerkleProofItem.Right r) {
                result = hasher.hash(Bytes.concat(recursiveVerifyProof(item.getLeft(), itemHash, hashes), r.getHash()));
            }
        }

        return result;
    }

    private static MerkleNode createNode(List<String> items, Integer depth) {
        if (items.size() == 1) {
            return new MerkleLeaf(items.stream().findFirst().get(), depth, hasher);
        } else {
            var divided = Lists.partition(items, items.size() / 2);
            var left = createNode(divided.getFirst(), depth + 1);
            var right = createNode(divided.getLast(), depth + 1);

            return new MerkleNode(left, right, depth, hasher);
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
