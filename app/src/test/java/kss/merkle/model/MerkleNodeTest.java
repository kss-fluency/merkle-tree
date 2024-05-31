package kss.merkle.model;

import kss.merkle.crypto.Hasher;
import kss.merkle.exception.MerkleException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class MerkleNodeTest {

    private static final Hasher mockHasher = Mockito.mock(Hasher.class);
    private static final byte[] SOME_HASH = new byte[]{1};
    private static final byte[] SOME_OTHER_HASH = new byte[]{2};

    private final Integer SOME_DEPTH = 2;
    private final String SOME_DATA = "0xdeadbeef";
    private final String SOME_OTHER_DATA = "0xfeedbabe";
    private final MerkleLeaf SOME_NODE;
    private final MerkleNode SOME_OTHER_NODE;

    public MerkleNodeTest() throws MerkleException {
        SOME_OTHER_NODE = new MerkleLeaf(SOME_OTHER_DATA, SOME_DEPTH, mockHasher);
        SOME_NODE = new MerkleLeaf(SOME_DATA, SOME_DEPTH, mockHasher);
    }

    @BeforeClass
    public static void mockHasher() {
        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_HASH);
    }

    @After
    public void cleanup() {
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

    @Test
    public void shouldUpdateHash() {
        MerkleNode node = new MerkleNode(SOME_NODE, SOME_OTHER_NODE, SOME_DEPTH, mockHasher);
        assertThat(node.getHash()).isEqualTo(SOME_HASH);

        Mockito.when(mockHasher.hash(any())).thenReturn(SOME_OTHER_HASH);
        node.updateHash();
        assertThat(node.getHash()).isEqualTo(SOME_OTHER_HASH);
    }
}