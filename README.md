# Chess
My newest iteration of my 3-year chess project. It comes with a AI Engine, a socket network (for LAN play), and AES Encryption. 

Powerful Optimizations:

-Rather than generating new Grid objects for every 
search level in AlphaBeta/MinMax, we can just modify the Grid objects
in place. This improvement made my Chess Engine speed
increase from 20,000-30,000 positions per second to around
450,000-1,000,000 positions per second! (Done)

-This will be messy to implement, but 
instead of allocating temporary lists of tiles
for moves, we generate the tiles (moves) at the same time
we score them in the AlphaBeta/MinMax,
which, if successful, would effectively double 
Chess Engine speed! 

-Using a table to store evaluation scores avoids
unnecessary calculations. However, table lookup
and saving cloned Grid objects to avoid
memory reference modification, is expensive and 
consumes a lot of memory.

To eliminate redundant tree searches, another table 
could be used which stores a Grid & Search Depth as a Key
and a Score as a Value. This eliminates entire tree branches, which means 
the Engine may be able to search much farther!

