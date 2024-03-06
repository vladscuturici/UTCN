    library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity maincontrol is
    Port(opcode : in std_logic_vector(2 downto 0);
         regdst : out std_logic;
         extop : out std_logic;
         alusrc : out std_logic;
         branch : out std_logic;
         jump : out std_logic;
         aluop : out std_logic_vector(2 downto 0);
         memwrite : out std_logic;
         memtoreg : out std_logic;
         regwrite : out std_logic);
end maincontrol;

architecture Behavioral of maincontrol is

begin
    process(opcode)
    begin
        regdst <= '0';
        extop <= '0';
        alusrc <= '0';
        branch <= '0';
        jump <= '0';
        aluop <= "000";
        memwrite <= '0';
        memtoreg <= '0';
        case (opcode) is 
            when "000" => -- R 
                RegDst <= '1';
                RegWrite <= '1';
                ALUOp <= "000";
            when "001" => -- ADDI
                ExtOp <= '1';
                ALUSrc <= '1';
                RegWrite <= '1';
                ALUOp <= "001";
            when "010" => -- LW
                ExtOp <= '1';
                ALUSrc <= '1';
                MemtoReg <= '1';
                RegWrite <= '1';
                ALUOp <= "001";
            when "011" => -- SW
                ExtOp <= '1';
                ALUSrc <= '1';
                MemWrite <= '1';
                ALUOp <= "001";
            when "100" => -- BEQ
                ExtOp <= '1';
                Branch <= '1';
                ALUOp <= "010";
            when "101" => -- ANDI
                ALUSrc <= '1';
                RegWrite <= '1';
                ALUOp <= "101";
            when "110" => -- ORI
                ALUSrc <= '1';
                RegWrite <= '1';
                ALUOp <= "110";
            when "111" => -- J
                Jump <= '1';
            when others => 
                RegDst <= '0'; ExtOp <= '0'; ALUSrc <= '0'; 
                Branch <= '0'; Jump <= '0'; MemWrite <= '0';
                MemtoReg <= '0'; RegWrite <= '0';
                ALUOp <= "000";
        end case;
    end process;    
end Behavioral;

    
