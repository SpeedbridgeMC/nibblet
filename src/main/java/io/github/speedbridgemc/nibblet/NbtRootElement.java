package io.github.speedbridgemc.nibblet;

/**
 * The base interface for NBT elements that can be <em>directly</em> serialized (written to NBT files).<p>
 * Other NBT elements can only be serialized <em>indirectly</em> - by being children of these root tags.
 */
public interface NbtRootElement extends NbtElement { }
