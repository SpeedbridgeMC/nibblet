package io.github.speedbridgemc.nibblet;

/**
 * The base interface for tags that can be <em>directly</em> serialized (written to NBT files).<p>
 * Other tags can only be serialized <em>indirectly</em> - by being children of these root tags.
 */
public interface RootTag extends Tag { }
