package kss.merkle.model;

import kss.merkle.crypto.Hasher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class MerkleNodeTest {

    private static final Hasher mockHasher = Mockito.mock(Hasher.class);
    private static final byte[] SOME_HASH = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

    private final Integer SOME_DEPTH = 2;
    private final String SOME_DATA = "0xdeadbeef";
    private final String SOME_OTHER_DATA = "0xfeedbabe";
    private final MerkleLeaf SOME_NODE = new MerkleLeaf(SOME_DATA, SOME_DEPTH, mockHasher);
    private final MerkleNode SOME_OTHER_NODE = new MerkleLeaf(SOME_OTHER_DATA, SOME_DEPTH, mockHasher);

    @BeforeClass
    public static void mockHasher() {
        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_HASH);
    }

    @Test
    public void shouldCreateNodeAndHashData() {
        MerkleNode node = new MerkleNode(SOME_NODE, SOME_OTHER_NODE, SOME_DEPTH, mockHasher);

        assertThat(node.getDepth()).isEqualTo(SOME_DEPTH);
        assertThat(node.getHash()).isEqualTo(SOME_HASH);
        assertThat(node.getLeft()).isEqualTo(SOME_NODE);
        assertThat(node.getRight()).isEqualTo(SOME_OTHER_NODE);
    }
}