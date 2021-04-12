# Nibblet
Libre, open-source implementation of Minecraft's NBT data format.  

This implementation is _not_ based off any other implementation (_especially_ not Minecraft's own - that would be illegal!)  
Instead, it was written for scratch, with the only reference used being the [wiki.vg page on the NBT format](https://wiki.vg/NBT).

**Note:** This library does _not_ handle reading or writing compressed (GZipped) NBT. You'll have to (de)compress the data yourself before using `TagIO`.
