library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity test_env is
    Port ( clk : in STD_LOGIC;
           btn : in STD_LOGIC_VECTOR (4 downto 0);
           sw : in STD_LOGIC_VECTOR (15 downto 0);
           led : out STD_LOGIC_VECTOR (15 downto 0);
           an : out STD_LOGIC_VECTOR (3 downto 0);
           cat : out STD_LOGIC_VECTOR (6 downto 0));
end test_env;

architecture Behavioral of test_env is

component Monoimpuls is
    Port ( btn : in STD_LOGIC;
           clk : in STD_LOGIC;
           en : out STD_LOGIC);
end component;

component instr_fetch is
    Port(clk : in STD_LOGIC;
        rst : in STD_LOGIC;
        BranchAddress : in STD_LOGIC_VECTOR(15 downto 0);
        JumpAddress: in STD_LOGIC_VECTOR(15 downto 0);
        Jump: in STD_LOGIC;
        PCSrc: in STD_LOGIC;
        EN: in STD_LOGIC;
        Instruction: out STD_LOGIC_VECTOR(15 downto 0);
        PCplus1: out STD_LOGIC_VECTOR(15 downto 0));
end component;

component ID is
    Port ( clk : in std_logic;
           en : in std_logic;
           instr : in std_logic_vector (12 downto 0);
           wd : in std_logic_vector (15 downto 0);
           regwrite : in std_logic;
           regdst : in std_logic;
           extop : in std_logic;
           rd1 : out std_logic_vector (15 downto 0);
           rd2 : out std_logic_vector (15 downto 0);
           ext_imm : out std_logic_vector (15 downto 0);
           func : out std_logic_vector (2 downto 0);
           sa : out std_logic);       
end component;

component maincontrol is
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
end component;

component UnitateExecutie is
    Port(RD1 : in std_logic_vector(15 downto 0);
         AluSrc : in std_logic;
         RD2 : in std_logic_vector(15 downto 0);
         ExtImm : in std_logic_vector(15 downto 0);
         sa : in std_logic;
         func : in std_logic_vector(2 downto 0);
         AluOp : in std_logic_vector(2 downto 0);
         PcPlus1 : in std_logic_vector(15 downto 0);
         Zero : out std_logic;
         AluRes : out std_logic_vector(15 downto 0);
         BranchAdress : out std_logic_vector(15 downto 0));
end component;

component mem is
    Port (MemWrite : IN std_logic;
          AluRes : IN std_logic_vector(15 downto 0);
          RD2 : IN std_logic_vector(15 downto 0);
          CLK : IN std_logic;
          MPG_EN : IN std_logic;
          AluRes2 : OUT std_logic_vector(15 downto 0);
          MemData : OUT std_logic_vector(15 downto 0)
          );
end component;

component SSD is
    Port ( digit : in STD_LOGIC_VECTOR (15 downto 0);
           clk : in STD_LOGIC;
           cat : out STD_LOGIC_VECTOR (6 downto 0);
           an : out STD_LOGIC_VECTOR (3 downto 0));
end component;

signal en, rst : std_logic;
signal BranchAddress, JumpAddress, Instruction, PCplus1, pc : std_logic_vector(15 downto 0);
signal Jump, PCSrc : std_logic;
signal regwrite, regdst, extop, sa : std_logic;
signal wd, rd1, rd2, ext_imm : std_logic_vector(15 downto 0);
signal func : std_logic_vector(2 downto 0);
signal alusrc, branch, memwrite, memtoreg : std_logic;
signal aluop : std_logic_vector(2 downto 0);
signal Zero : std_logic;
signal AluRes, AluRes2, memdata : std_logic_vector(15 downto 0);
signal digit : std_logic_vector(15 downto 0);

begin
    MPG1: Monoimpuls port map(btn(0), clk, en);
    MPG2: Monoimpuls port map(btn(1), clk, rst);
    instruction_fetch: instr_fetch port map(clk,rst, BranchAddress, JumpAddress, jump,  PCSrc, en, Instruction, PCplus1);
    instruction_decode: ID port map(clk, en, Instruction(12 downto 0), wd, regwrite, regdst, extop, rd1, rd2, ext_imm, func, sa);
    main_control: maincontrol port map(Instruction(15 downto 13), RegDst, ExtOp, alusrc, branch, jump, aluop, memwrite, memtoreg, regwrite);
    unit_ex: UnitateExecutie port map(rd1, alusrc, rd2, Ext_imm, sa, func, aluop, PCplus1, Zero, alures, BranchAddress); 
    mem_port_map: MEM port map(memwrite, alures, rd2, clk, en, alures2, memdata);
    WD <= ALURes2 when memtoreg = '0' else memdata;
    pcsrc <= Zero and Branch;
    JumpAddress <= PCplus1(15 downto 13) & Instruction(12 downto 0);
    with sw(7 downto 5) select
        digit <=  Instruction when "000", 
                   PCplus1 when "001",
                   rd1 when "010",
                   rd2 when "011",
                   ext_imm when "100",
                   ALURes when "101",
                   memdata when "110",
                   wd when "111",
                   (others => '0') when others; 
    SSD_port_map : SSD port map(digit, clk, cat, an);
    led(10 downto 0) <= aluop & regdst & extop & alusrc & branch & jump & memwrite & memtoreg & RegWrite;
end Behavioral;

    

    

    
