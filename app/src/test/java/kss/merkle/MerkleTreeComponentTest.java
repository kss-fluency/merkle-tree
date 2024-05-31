package kss.merkle;

import com.google.common.io.BaseEncoding;
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

public class MerkleTreeComponentTest {

    private final static List<String> TREE_DATA = List.of(
            "0x61b7209e", "0x7b7e1b8b", "0x62ab3c0f", "0x72c04a10",
            "0x408440e9", "0x5b5d5053", "0x3a9aae68", "0x49e6f1c4",
            "0x35a1be5d", "0x6f6f6d57", "0x12aa9202", "0x11799a61",
            "0x32c82baa", "0x57735a17", "0x6197a3c9", "0x2b6b03da"
    );

    private final static List<MerkleProofItem> PROOF_FOR_ITEM_FOUR_IN_TREE_DATA = List.of(
            new MerkleProofItem.Right(BaseEncoding.base16().lowerCase().decode("900a49e2dad814fdcca81adcab818ebbec5055804a10e3a6e504c27cbce35a03")),
            new MerkleProofItem.Right(BaseEncoding.base16().lowerCase().decode("71d210f1b10b051e906e103b2a5fb37bceca8e19c3b744dc4fc3ec2ef52eaca8")),
            new MerkleProofItem.Left(BaseEncoding.base16().lowerCase().decode("dcd335636aa27edf14b8e693c1d76830833e13ecdf464a948a1c70b5a0e84388")),
            new MerkleProofItem.Left(BaseEncoding.base16().lowerCase().decode("c260b4d9de200760299f1d774fb05e0a17f0a108f3db91d3f13c9686e5bc89ce"))
    );

    private final static String SOME_INVALID_ITEM = "0xdeadbeef";

    @Test
    public void shouldCreateTreeFromList() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);
        MerkleNode node = tree.getRoot();

        assertThat(tree.getSize()).isEqualTo(TREE_DATA.size());

        while (!(node instanceof MerkleLeaf)) node = node.getLeft();
        assertThat(((MerkleLeaf) node).getData()).isEqualTo(TREE_DATA.getFirst());

        node = tree.getRoot();
        while (!(node instanceof MerkleLeaf)) node = node.getRight();
        assertThat(((MerkleLeaf) node).getData()).isEqualTo(TREE_DATA.getLast());
    }

    @Test
    public void shouldAssignNodeDepths() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);
        MerkleNode node = tree.getRoot();
        int i = 0;

        assertThat(node.getDepth()).isEqualTo(i++);
        do {
            node = node.getLeft();
            assertThat(node.getDepth()).isEqualTo(i++);
        } while (!(node instanceof MerkleLeaf));

        assertThat((double) i).isEqualTo(Math.sqrt(TREE_DATA.size()) + 1);
    }

    @Test
    public void shouldNotCreateTreeWithInvalidSize() {
        ArrayList<String> invalidTree = new ArrayList<>(TREE_DATA);
        invalidTree.removeFirst();

        assertThatThrownBy(() -> MerkleTree.fromList(invalidTree))
                .isInstanceOf(MerkleException.class)
                .hasMessage(String.format("Merkle Tree can only be built using n^2 items but you provided %d", invalidTree.size()))
        ;
    }

    @Test
    public void shouldGenerateProof() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);
        String item = TREE_DATA.get(3); // 0x72c04a10
        List<MerkleProofItem> proof = tree.generateProof(item);

        // path to the correct leaf is: Left, Left, Right, Right. we need hashes for opposite nodes
        // this is illustrated in MerkleTreeTest-tree.pdf in test/resources dir
        assertThat(proof.getFirst()).isInstanceOf(MerkleProofItem.Right.class);
        assertThat(proof.getFirst().getHash()).isEqualTo(tree.getRoot().getRight().getHash());
        assertThat(proof.get(1)).isInstanceOf(MerkleProofItem.Right.class);
        assertThat(proof.get(1).getHash()).isEqualTo(tree.getRoot().getLeft().getRight().getHash());
        assertThat(proof.get(2)).isInstanceOf(MerkleProofItem.Left.class);
        assertThat(proof.get(2).getHash()).isEqualTo(tree.getRoot().getLeft().getLeft().getLeft().getHash());
        assertThat(proof.get(3)).isInstanceOf(MerkleProofItem.Left.class);
        assertThat(proof.get(3).getHash()).isEqualTo(tree.getRoot().getLeft().getLeft().getRight().getLeft().getHash());
    }

    @Test
    public void shouldThrowExceptionWhenGeneratingProofForInvalidItem() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);

        assertThatThrownBy(() -> tree.generateProof(SOME_INVALID_ITEM))
                .isInstanceOf(MerkleException.class)
                .hasMessage("Item not found in the tree")
        ;
    }

    @Test
    public void shouldVerifyProof() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);
        String item = TREE_DATA.get(3); // 0x72c04a10
        assertThat(tree.verifyProof(item, new ArrayList<>(PROOF_FOR_ITEM_FOUR_IN_TREE_DATA))).isTrue();
    }

    @Test
    public void shouldNotVerifyProofForInvalidItem() throws MerkleException {
        MerkleTree tree = MerkleTree.fromList(TREE_DATA);
        assertThat(tree.verifyProof(SOME_INVALID_ITEM, new ArrayList<>(PROOF_FOR_ITEM_FOUR_IN_TREE_DATA))).isFalse();
        assertThat(tree.verifyProof(TREE_DATA.getFirst(), new ArrayList<>(PROOF_FOR_ITEM_FOUR_IN_TREE_DATA))).isFalse();
    }
}