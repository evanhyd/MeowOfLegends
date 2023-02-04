package unboxthecat.meowoflegends;

public interface MOLComponent {
    /**
     * Called when attaching to the MOLEntity.
     * @param owner the MOLEntity.
     */
    void onAttach(MOLEntity owner);

    /**
     * Called when removing from the MOLEntity.
     * @param owner the MOLEntity.
     */
    void onRemove(MOLEntity owner);
}
