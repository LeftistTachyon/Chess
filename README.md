# Chess
My newest iteration of my 3-year chess project. It comes with a AI Engine, a socket network (for LAN play), and AES Encryption. 

WOW, Using a table to store evalution scores speeds up my AI 
by a few hundred thousand positions per second!

To eliminate redudant tree searches, I might make
another table that store a board & depth as a key
and a score as a value. This eliminates 
entire tree branches, which means I can search 
much farther!


Currently, I'm working on... 

-Fully implementing En Passant (Done)

-A faster search algorithm that does not create temporary move lists (which is slow)

-the FastAI folder to make my AI faster by using bitboards instead of objects.
