----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 12/11/2023 05:43:59 PM
-- Design Name: 
-- Module Name: comp - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity comp is
  Port ( A : in std_logic_vector (7 downto 0);
         B : in std_logic_vector (7 downto 0);  
         lower : out std_logic;
         equal : out std_logic;
         higher : out std_logic
         );
end comp;

architecture Behavioral of comp is

begin

    process(A, B)
    begin
       if A < B then
            lower <= '1';
            equal <= '0';
            higher <= '0';
      elsif A = B then
            lower <= '0';
            equal <= '1';
            higher <= '0';
       else
            lower <= '0';
            equal <= '0';
            higher <= '1';
       end if;
    end process;

end Behavioral;
