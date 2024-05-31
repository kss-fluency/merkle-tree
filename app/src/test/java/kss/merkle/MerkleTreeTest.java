package kss.merkle;

import kss.merkle.exception.MerkleException;
import kss.merkle.model.MerkleLeaf;
import kss.merkle.model.MerkleNode;
import kss.merkle.model.MerkleProofItem;
import kss.merkle.model.MerkleTree;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MerkleTreeTest {

    private final static List<String> SOME_TREE_DATA = List.of(
            "0x61b7209e", "0x7b7e1b8b", "0x62ab3c0f", "0x72c04a10",
            "0x408440e9", "0x5b5d5053", "0x3a9aae68", "0x49e6f1c4",
            "0x35a1be5d", "0x6f6f6d57", "0x12aa9202", "0x11799a61",
            "0x32c82baa", "0x57735a17", "0x6197a3c9", "0x2b6b03da"
    );

    @Test
    public void shouldCreateTreeFromList() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(SOME_TREE_DATA);
        MerkleNode node = tree.getRoot();

        assertThat(tree.getSize()).isEqualTo(SOME_TREE_DATA.size());

        while (!(node instanceof MerkleLeaf)) node = node.getLeft();
        assertThat(((MerkleLeaf) node).getData()).isEqualTo(SOME_TREE_DATA.getFirst());

        node = tree.getRoot();
        while (!(node instanceof MerkleLeaf)) node = node.getRight();
        assertThat(((MerkleLeaf) node).getData()).isEqualTo(SOME_TREE_DATA.getLast());
    }

    @Test
    public void shouldAssignNodeDepths() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(SOME_TREE_DATA);
        MerkleNode node = tree.getRoot();
        int i = 0;

        assertThat(node.getDepth()).isEqualTo(i++);
        do {
            node = node.getLeft();
            assertThat(node.getDepth()).isEqualTo(i++);
        } while (!(node instanceof MerkleLeaf));

        assertThat((double) i).isEqualTo(Math.sqrt(SOME_TREE_DATA.size()) + 1);
    }

    @Test
    public void shouldNotCreateTreeWithInvalidSize() {
        ArrayList<String> invalidTree = new ArrayList<>(SOME_TREE_DATA);
        invalidTree.removeFirst();

        assertThatThrownBy(() -> MerkleTree.fromList(invalidTree))
                .isInstanceOf(MerkleException.class)
                .hasMessage(String.format("Merkle Tree can only be built using n^2 items but you provided %d", invalidTree.size()))
        ;
    }

    @Test
    public void shouldGenerateProof() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(SOME_TREE_DATA);
        String item = SOME_TREE_DATA.get(3); // 0x72c04a10
        List<MerkleProofItem> proof = tree.generateProof(item);

        // path to the correct leaf is: Left, Left, Right, Right. we need hashes for opposite nodes
        // this is illustrated in tree.pdf in test/resources dir
        assertThat(proof.getFirst()).isInstanceOf(MerkleProofItem.Right.class);
        assertThat(proof.getFirst().getHash()).isEqualTo(tree.getRoot().getRight().getHash());
        assertThat(proof.get(1)).isInstanceOf(MerkleProofItem.Right.class);
        assertThat(proof.get(1).getHash()).isEqualTo(tree.getRoot().getLeft().getRight().getHash());
        assertThat(proof.get(2)).isInstanceOf(MerkleProofItem.Left.class);
        assertThat(proof.get(2).getHash()).isEqualTo(tree.getRoot().getLeft().getLeft().getLeft().getHash());
        assertThat(proof.get(3)).isInstanceOf(MerkleProofItem.Left.class);
        assertThat(proof.get(3).getHash()).isEqualTo(tree.getRoot().getLeft().getLeft().getRight().getLeft().getHash());
    }
}