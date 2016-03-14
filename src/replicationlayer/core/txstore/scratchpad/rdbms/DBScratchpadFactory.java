package replicationlayer.core.txstore.scratchpad.rdbms;

interface DBScratchpadFactory extends replicationlayer.core.txstore.scratchpad.ScratchpadFactory{

    // releases the scratchpad
    public void releaseScratchpad();
}