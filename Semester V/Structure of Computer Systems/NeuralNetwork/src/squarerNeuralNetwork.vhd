library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity squarerNeuralNetwork is
    Port (
        A : in std_logic_vector(7 downto 0);
        CLK : in std_logic;
        F : out std_logic_vector(7 downto 0)
    );
end squarerNeuralNetwork;

architecture Behavioral of squarerNeuralNetwork is

    component neuron is
        port(
            inputs : in std_logic_vector(7 downto 0);
            weights : in std_logic_vector(7 downto 0);
            clk : in std_logic;
            output : out std_logic
        );
    end component;

    signal input_layer_outputs : std_logic_vector(7 downto 0);
    signal hidden_layer_outputs : std_logic_vector(7 downto 0);
    signal output_layer_outputs : std_logic_vector(7 downto 0);
    
    type weight_type is array(0 to 7) of std_logic_vector(7 downto 0);
    
    signal weights_input_layer: weight_type := (
        "01011110", 
        "01111110", 
        "11111010",
        "10000110", 
        "11011011",
        "10111111",
        "01111011",
        "11011111"
    );
    signal weights_hidden_layer: weight_type := (
        "10110010",
        "01010010",
        "11111011",
        "11010111",
        "01101101",
        "10111110",
        "01110011",
        "10011001"
    );
    signal weights_output_layer: weight_type := (
        "01110000",
        "00111001",
        "10111101",
        "00011101",
        "11001001",
        "00100110",
        "11110011",
        "11101001"
    );

begin

    input_layer: for i in 0 to 7 generate
        input_neuron: neuron port map(
            inputs => A, 
            weights => weights_input_layer(i), 
            clk => clk,
            output => input_layer_outputs(i) 
        );
    end generate;

    hidden_layer: for i in 0 to 7 generate
        hidden_neuron: neuron port map(
            inputs => input_layer_outputs, 
            weights => weights_hidden_layer(i), 
            clk => clk,
            output => hidden_layer_outputs(i)
        );
    end generate;

    output_layer: for i in 0 to 7 generate
        output_neuron: neuron port map(
            inputs => hidden_layer_outputs, 
            weights => weights_output_layer(i),
            clk => clk,
            output => output_layer_outputs(i) 
        );
    end generate;

    F <= output_layer_outputs;

end Behavioral;
