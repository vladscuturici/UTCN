----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 12/11/2023 05:44:38 PM
-- Design Name: 
-- Module Name: main - Behavioral
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

entity main is
  Port ( N : in std_logic_vector(7 downto 0);
         Start : in std_logic;
         Reset : in std_logic;
         CLK : in std_logic;   
         Prime : out std_logic;
         Done : out std_logic
        );
end main;

architecture Behavioral of main is

component counter is
  Port ( N : in std_logic_vector(7 downto 0);
         CLK : in std_logic;
         EN : in std_logic;
         Reset : in std_logic;
         CNT : out std_logic_vector(7 downto 0)
        );
end component;

component squarerNeuralNetwork is
  Port (A : in std_logic_vector (7 downto 0);
        CLK : in std_logic;
        F : out std_logic_vector (7 downto 0)
        );
end component;

component reminderNeuralNetwork is
  Port ( A : in std_logic_vector (7 downto 0);
         B : in std_logic_vector (7 downto 0);
         CLK : in std_logic;
         F : out std_logic_vector (7 downto 0)
         );
end component;

component comp is
  Port ( A : in std_logic_vector (7 downto 0);
         B : in std_logic_vector (7 downto 0);  
         lower : out std_logic;
         equal : out std_logic;
         higher : out std_logic
         );
end component;

signal en_cnt, rst_cnt : std_logic;
signal h1, h2, l1, l2, e1, e2 : std_logic;
signal cnt, F_sq, F_rem : std_logic_vector(7 downto 0);

begin

    counter_process: counter 
    port map (
        N => N, 
        CLK => CLK, 
        EN => en_cnt, 
        Reset => rst_cnt, 
        CNT => cnt
    );

    sq_nn: squarerNeuralNetwork 
    port map (
        A => cnt, 
        CLK => CLK, 
        F => F_sq
    );
    
    comp1: comp 
    port map (
        A => F_sq, 
        B => N, 
        lower => l1, 
        equal => e1, 
        higher => h1
    );
    
    rem_nn: reminderNeuralNetwork 
    port map (
        A => N, 
        B => cnt, 
        CLK => CLK, 
        F => F_rem
    );
    
    comp2: comp 
    port map (
        A => F_rem, 
        B => "00000000", 
        lower => l2, 
        equal => e2, 
        higher => h2
    );
    main_process: process(CLK, Start)
    begin
        if rising_edge(CLK) then
            if Start = '1' then
                en_cnt <= not (h1 or e2);
                rst_cnt <= Reset or h1 or e2;
                Done <= h1 or e2;
                Prime <= not e2;
            end if;
        end if;
    end process;
    
end Behavioral;
