.386
.model flat, stdcall
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;includem biblioteci, si declaram ce functii vrem sa importam
includelib msvcrt.lib
extern exit: proc
extern malloc: proc
extern memset: proc

includelib canvas.lib
extern BeginDrawing: proc
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;declaram simbolul start ca public - de acolo incepe executia
public start
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;sectiunile programului, date, respectiv cod
.data
;aici declaram date
window_title DB "X si 0",0
area_width EQU 640
area_height EQU 480
area DD 0

counter DD 0 ; numara evenimentele de tip timer
counterReset DD 0 

arg1 EQU 8
arg2 EQU 12
arg3 EQU 16
arg4 EQU 20

symbol_width EQU 10
symbol_height EQU 20
include digits.inc
include letters.inc

table_x EQU 320
table_y EQU 160
table_size EQU 240

reset_x EQU 400
reset_y EQU 100
reset_width EQU 80
reset_height EQU 20

aibutton_x EQU 80
aibutton_y EQU 320
aibutton_width EQU 160
aibutton_height EQU 80


t11 DD 0
t12 DD 0
t13 DD 0
t21 DD 0
t22 DD 0
t23 DD 0
t31 DD 0
t32 DD 0
t33 DD 0

x11 EQU 320
x12 EQU 400
x13 EQU 480
x21 EQU 320
x22 EQU 400
x23 EQU 480
x31 EQU 320
x32 EQU 400
x33 EQU 480

y11 EQU 160
y12 EQU 160
y13 EQU 160
y21 EQU 240
y22 EQU 240
y23 EQU 240
y31 EQU 320
y32 EQU 320
y33 EQU 320

AI DD 0
AI_turn DD 1

game_over DD 0

player DD 1

outside_table DD 0

culoare_simb DD 0

.code
; procedura make_text afiseaza o litera sau o cifra la coordonatele date
; arg1 - simbolul de afisat (litera sau cifra)
; arg2 - pointer la vectorul de pixeli
; arg3 - pos_x
; arg4 - pos_y

make_text proc
	push ebp
	mov ebp, esp
	pusha
	
	mov eax, [ebp+arg1] ; citim simbolul de afisat
	cmp eax, 'A'
	jl make_digit
	cmp eax, 'Z'
	jg make_digit
	sub eax, 'A'
	lea esi, letters
	jmp draw_text
make_digit:
	cmp eax, '0'
	jl make_space
	cmp eax, '9'
	jg make_space
	sub eax, '0'
	lea esi, digits
	jmp draw_text
make_space:	
	mov eax, 26 ; de la 0 pana la 25 sunt litere, 26 e space
	lea esi, letters
	
draw_text:
	mov ebx, symbol_width
	mul ebx
	mov ebx, symbol_height
	mul ebx
	add esi, eax
	mov ecx, symbol_height
bucla_simbol_linii:
	mov edi, [ebp+arg2] ; pointer la matricea de pixeli
	mov eax, [ebp+arg4] ; pointer la coord y
	add eax, symbol_height
	sub eax, ecx
	mov ebx, area_width
	mul ebx
	add eax, [ebp+arg3] ; pointer la coord x
	shl eax, 2 ; inmultim cu 4, avem un DWORD per pixel
	add edi, eax
	push ecx
	mov ecx, symbol_width
bucla_simbol_coloane:
	cmp byte ptr [esi], 0
	je simbol_pixel_alb
	mov dword ptr [edi], 0
	jmp simbol_pixel_next
simbol_pixel_alb:
	mov dword ptr [edi], 0FFFFFFh
simbol_pixel_next:
	inc esi
	add edi, 4
	loop bucla_simbol_coloane
	pop ecx
	loop bucla_simbol_linii
	popa
	mov esp, ebp
	pop ebp
	ret
make_text endp

; un macro ca sa apelam mai usor desenarea simbolului
make_text_macro macro symbol, drawArea, x, y
	push y
	push x
	push drawArea
	push symbol
	call make_text
	add esp, 16
endm
line_horizontal macro x, y, len, color
local bucla_line
	mov eax, y
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, len
bucla_line:
	mov dword ptr[eax], color
	add eax, 4
	loop bucla_line
endm

line_vertical macro x, y, len, color
local bucla_line
	mov eax, y
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, len
bucla_line:
	mov dword ptr[eax], color
	add eax, area_width * 4
	loop bucla_line
endm

line_diagonal macro x, y, len, color
local bucla_line
	mov eax, y
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, len
bucla_line:
	
	mov dword ptr[eax], color
	add eax, area_width * 4
	add eax, 4
	loop bucla_line
endm

line_diagonal2 macro x, y, len, color
local bucla_line
	mov eax, y
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, len
bucla_line:
	
	mov dword ptr[eax], color
	add eax, area_width * 4
	sub eax, 4
	loop bucla_line
endm

make_X macro x, y, color
local bucla_x
	mov eax, y
	add eax, 8
	mov ebx,area_width
	mul ebx
	add eax, x
	add eax, 8
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, 66
	mov ebx, 256
	shr esi, 2
bucla_x:
	mov dword ptr[eax], color
	mov dword ptr[eax+4], color
	mov dword ptr[eax-4], color
	mov dword ptr[eax+8], color
	mov dword ptr[eax-8], color
	
	add eax, ebx
	mov dword ptr[eax], color
	mov dword ptr[eax+4], color
	mov dword ptr[eax-4], color
	mov dword ptr[eax+8], color
	mov dword ptr[eax-8], color
	sub eax, ebx
	sub ebx, 8
	
	add eax, area_width * 4
	add eax, 4
	loop bucla_x
endm


clear_table macro x, y, color
local bucla_x
	mov eax, y
	sub eax, 10
	add eax, 8
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	mov ecx, 153600
	;shl ecx, 2
	clear:
		mov dword ptr[eax], color
		add eax, 4
	loop clear
endm


make_0 macro x, y, color
local bucla_0, bucla_0_2
	mov eax, y
	mov ebx,area_width
	mul ebx
	add eax, x
	shl eax, 2 ;aici e inmultirea cu 4, pentru a face formula finala
	add eax, area
	add eax, 40*area_width
	add eax, 80
	mov ebx, eax
	mov ecx, 60
bucla_0:
	mov dword ptr[eax-4], color
	mov dword ptr[eax+156], color
	mov dword ptr[eax], color
	mov dword ptr[eax+160], color
	mov dword ptr[eax+4], color
	mov dword ptr[eax+164], color
	add eax, area_width * 4
	loop bucla_0

mov ecx, 40
bucla_0_2:
	mov dword ptr[ebx-4*area_width], color
	mov dword ptr[ebx], color
	mov dword ptr[ebx+4*area_width], color
	mov dword ptr[ebx+236*area_width], color
	mov dword ptr[ebx+240*area_width], color
	mov dword ptr[ebx+244*area_width], color
	add ebx, 4
	loop bucla_0_2
endm

verify_x:
	;linii
	xor eax, eax
	cmp t11, 1
	jne l1
	cmp t12, 1
	jne l1
	cmp t13, 1
	jne l1
	line_horizontal x11, y11+36, 240, 0
	line_horizontal x11, y11+40, 240, 0
	line_horizontal x11, y11+44, 240, 0
	mov game_over, 1
	mov eax, 1
	l1:
	cmp t21, 1
	jne l2
	cmp t22, 1
	jne l2
	cmp t23, 1
	jne l2
	line_horizontal x11, y21+36, 240, 0
	line_horizontal x11, y21+40, 240, 0
	line_horizontal x11, y21+44, 240, 0
	mov game_over, 1
	mov eax, 1
	l2:
	cmp t31, 1
	jne l3
	cmp t32, 1
	jne l3
	cmp t33, 1
	jne l3
	line_horizontal x11, y31+36, 240, 0
	line_horizontal x11, y31+40, 240, 0
	line_horizontal x11, y31+44, 240, 0
	mov game_over, 1
	mov eax, 1
	l3:
	;coloane
	cmp t11, 1
	jne c1
	cmp t21, 1
	jne c1
	cmp t31, 1
	jne c1
	line_vertical x11+36, y11, 240, 0
	line_vertical x11+40, y11, 240, 0
	line_vertical x11+44, y11, 240, 0
	mov game_over, 1
	mov eax, 1
	c1:
	cmp t12, 1
	jne c2
	cmp t22, 1
	jne c2
	cmp t32, 1
	jne c2
	line_vertical x11+116, y11, 240, 0
	line_vertical x11+120, y11, 240, 0
	line_vertical x11+124, y11, 240, 0
	mov game_over, 1
	mov eax, 1
	c2:
	cmp t13, 1
	jne c3
	cmp t23, 1
	jne c3
	cmp t33, 1
	jne c3
	line_vertical x11+196, y11, 240, 0
	line_vertical x11+200, y11, 240, 0
	line_vertical x11+204, y11, 240, 0
	mov game_over, 1
	mov eax, 1
	c3:
	;diagonale
	cmp t11, 1
	jne d1
	cmp t22, 1
	jne d1
	cmp t33, 1
	jne d1
	line_diagonal x11, y11, 240, 0
	line_diagonal x11+8, y11, 230, 0
	line_diagonal x11-8+10, y11+10, 230, 0
	mov game_over, 1
	mov eax, 1
	d1:
	cmp t13, 1
	jne d2
	cmp t22, 1
	jne d2
	cmp t31, 1
	jne d2
	line_diagonal2 x13+80, y11, 240, 0
	line_diagonal2 x13+88-8, y11+8, 230, 0
	line_diagonal2 x13+72, y11, 230, 0
	mov game_over, 1
	mov eax, 1
	d2:
ret ;

verify_0:
	;linii
	xor eax, eax
	cmp t11, 2
	jne l11
	cmp t12, 2
	jne l11
	cmp t13, 2
	jne l11
	line_horizontal x11, y11+36, 240, 00000FFh
	line_horizontal x11, y11+40, 240, 00000FFh
	line_horizontal x11, y11+44, 240, 00000FFh
	mov game_over, 1
	mov eax, 1
	l11:
	cmp t21, 2
	jne l21
	cmp t22, 2
	jne l21
	cmp t23, 2
	jne l21
	mov eax, 1
	line_horizontal x11, y21+36, 240, 00000FFh
	line_horizontal x11, y21+40, 240, 00000FFh
	line_horizontal x11, y21+44, 240, 00000FFh
	mov game_over, 1
	l21:
	cmp t31, 2
	jne l31
	cmp t32, 2
	jne l31
	cmp t33, 2
	jne l31
	mov eax, 1
	line_horizontal x11, y31+36, 240, 00000FFh
	line_horizontal x11, y31+40, 240, 00000FFh
	line_horizontal x11, y31+44, 240, 00000FFh
	mov game_over, 1
	l31:
	;coloane
	cmp t11, 2
	jne c11
	cmp t21, 2
	jne c11
	cmp t31, 2
	jne c11
	mov eax, 1
	line_vertical x11+36, y11, 240, 00000FFh
	line_vertical x11+40, y11, 240, 00000FFh
	line_vertical x11+44, y11, 240, 00000FFh
	mov game_over, 1
	c11:
	cmp t12, 2
	jne c21
	cmp t22, 2
	jne c21
	cmp t32, 2
	jne c21
	line_vertical x11+116, y11, 240, 00000FFh
	line_vertical x11+120, y11, 240, 00000FFh
	line_vertical x11+124, y11, 240, 00000FFh
	mov game_over, 1
	mov eax, 1
	c21:
	cmp t13, 2
	jne c31
	cmp t23, 2
	jne c31
	cmp t33, 2
	jne c31
	line_vertical x11+196, y11, 240, 00000FFh
	line_vertical x11+200, y11, 240, 00000FFh
	line_vertical x11+204, y11, 240, 00000FFh
	mov game_over, 1
	mov eax, 1
	c31:
	;diagonale
	cmp t11, 2
	jne d11
	cmp t22, 2
	jne d11
	cmp t33, 2
	jne d11
	line_diagonal x11, y11, 240, 00000FFh
	line_diagonal x11+8, y11, 230, 00000FFh
	line_diagonal x11-8+10, y11+10, 230, 00000FFh
	mov game_over, 1
	mov eax, 1
	d11:
	cmp t13, 2
	jne d21
	cmp t22, 2
	jne d21
	cmp t31, 2
	jne d21
	line_diagonal2 x13+80, y11, 240, 00000FFh
	line_diagonal2 x13+88-8, y11+8, 230, 00000FFh
	line_diagonal2 x13+72, y11, 230, 00000FFh
	mov game_over, 1
	mov eax, 1
	d21:
ret ;

verify_draw:
	xor eax, eax
	cmp t11, 0
	je not_draw
	cmp t12, 0
	je not_draw
	cmp t13, 0
	je not_draw
	cmp t21, 0
	je not_draw
	cmp t22, 0
	je not_draw
	cmp t23, 0
	je not_draw
	cmp t31, 0
	je not_draw
	cmp t32, 0
	je not_draw
	cmp t33, 0
	je not_draw
	mov eax, 1
	not_draw:
ret ;

can_win:
	xor eax, eax
	cmp t11, 1
	jne not1
		cmp t13, 1
		jne not12
			cmp t12, 0
			jne not12
				mov eax, 12
				jmp final
		not12:
		cmp t31, 1
		jne not21
			cmp t21,0
			jne not21
				mov eax, 21
				jmp final
		not21:
		cmp t33, 1
		jne not22
			cmp t22,0
			jne not22
				mov eax, 22
				jmp final
		not22:
	not1:
	cmp t33, 1
	jne not3
		cmp t31, 1
		jne not31
			cmp t32, 0
			jne not31
				mov eax, 32
				jmp final
		not31:
		cmp t13, 1
		jne not23
			cmp t23, 0
			jne not23
				mov eax, 23
				jmp final
		not23:
	not3: 
	final:
ret;

can_prevent:
	xor eax, eax
	cmp t12, 2
	jne no1
		cmp t32, 2
		jne no1
			mov eax, 1
			jmp final0
	no1:
	
	cmp t21, 2
	jne no2
		cmp t23, 2
		jne no2
			mov eax, 1
			jmp final0
	no2:
	final0:
ret;

can_prevent32:
	xor eax, eax
	cmp t12, 2
	jne nup
		cmp t22, 2
		jne nup
			mov eax, 1
	nup:
ret;

; functia de desenare - se apeleaza la fiecare click
; sau la fiecare interval de 200ms in care nu s-a dat click
; arg1 - evt (0 - initializare, 1 - click, 2 - s-a scurs intervalul fara click)
; arg2 - x
; arg3 - y

draw proc
	push ebp
	mov ebp, esp
	pusha
	
	mov eax, [ebp+arg1]
	cmp eax, 1
	jz evt_click
	cmp eax, 2
	jz evt_timer ; nu s-a efectuat click pe nimic
	;mai jos e codul care intializeaza fereastra cu pixeli albi
	mov eax, area_width
	mov ebx, area_height
	mul ebx
	shl eax, 2
	push eax
	push 255
	push area
	call memset
	add esp, 12
	jmp afisare_litere
	
evt_click:

	mov eax, [ebp+arg2]
	
	cmp eax, aibutton_x
	jl aibutton_fail
	cmp eax, aibutton_x+aibutton_width
	jg aibutton_fail
	mov eax, [ebp+arg3]
	cmp eax, aibutton_y
	jl aibutton_fail
	cmp eax, aibutton_y+aibutton_height
	jg aibutton_fail
		mov AI, 1
	aibutton_fail:
	
	cmp eax, reset_x
	jl reset_fail
	cmp eax, reset_x+reset_width
	jg reset_fail
	mov eax, [ebp+arg3]
	cmp eax, reset_y
	jl reset_fail
	cmp eax, reset_y+reset_height
	jg reset_fail
	make_text_macro 'R', area, 406, 128
	make_text_macro 'E', area, 416, 128
	make_text_macro 'S', area, 426, 128
	make_text_macro 'E', area, 436, 128
	make_text_macro 'T', area, 446, 128 
	make_text_macro 'A', area, 456, 128 
	make_text_macro 'T', area, 466, 128	
	mov AI, 0
	mov counterReset, 0	
	mov player, 1
	mov game_over, 0
	mov t11, 0
	mov t12, 0
	mov t13, 0
	mov t21, 0
	mov t22, 0
	mov t23, 0
	mov t31, 0
	mov t32, 0
	mov t33, 0
	clear_table table_x,table_y,0FFFFFFh
	jmp reset_true
	reset_fail:
	make_text_macro ' ', area, 406, 128
	make_text_macro ' ', area, 416, 128
	make_text_macro ' ', area, 426, 128
	make_text_macro ' ', area, 436, 128
	make_text_macro ' ', area, 446, 128 
	make_text_macro ' ', area, 456, 128 
	make_text_macro ' ', area, 466, 128
	make_text_macro ' ', area, 100, 100
	
	make_text_macro ' ', area, aibutton_x+50, aibutton_y-80
	make_text_macro ' ', area, aibutton_x+60, aibutton_y-80
	make_text_macro ' ', area, aibutton_x+70, aibutton_y-80
	make_text_macro ' ', area, aibutton_x+80, aibutton_y-80
	make_text_macro ' ', area, aibutton_x+90, aibutton_y-80
	
	reset_true:
	cmp game_over, 1
	je evt_timer
	
	cmp AI, 1
	jne not_AI
	
	cmp player, 1
	je AI_move
	
	not_AI:
	
	mov eax, [ebp+arg2]
	cmp eax, x11
	jl next1
	cmp eax, x11+80
	jg next1
	mov eax, [ebp+arg3]
	cmp eax, y11
	jl next1
	cmp eax, y11+80
	jg next1
	cmp t11, 0
	jne next2
	cmp player, 1
	je turn_x1
		make_0 x11,y11,00000ffh
		mov player, 1
		mov t11, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next1		
	turn_x1:
		make_x x11,y11,0
		mov player, 2
		mov t11, 1
		call verify_x
		cmp eax, 1
		je win_x
	next1:
	
	mov eax, [ebp+arg2]
	cmp eax, x12+1
	jl next2
	cmp eax, x12+80
	jg next2
	mov eax, [ebp+arg3]
	cmp eax, y12+1
	jl next2
	cmp eax, y12+80
	jg next2
	cmp t12, 0
	jne next2
	cmp player, 1
	je turn_x2
		make_0 x12,y12,00000ffh
		mov player, 1
		mov t12, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next2		
	turn_x2:
		make_x x12,y12,0
		mov player, 2
		mov t12, 1
		call verify_x
		cmp eax, 1
		je win_x
	next2:
	mov eax, [ebp+arg2]
	cmp eax, x13+1
	jl next3
	cmp eax, x13+80
	jg next3
	mov eax, [ebp+arg3]
	cmp eax, y13+1
	jl next3
	cmp eax, y13+80
	jg next3
	cmp t13, 0
	jne next3
	cmp player, 1
	je turn_x3
		make_0 x13,y13,00000ffh
		mov player, 1
		mov t13, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next3		
	turn_x3:
		make_x x13,y13,0
		mov player, 2
		mov t13, 1
		call verify_x
		cmp eax, 1
		je win_x
	next3:
	
	mov eax, [ebp+arg2]
	cmp eax, x21+1
	jl next4
	cmp eax, x21+80
	jg next4
	mov eax, [ebp+arg3]
	cmp eax, y21+1
	jl next4
	cmp eax, y21+80
	jg next4
	cmp t21, 0
	jne next4
	cmp player, 1
	je turn_x4
		make_0 x21,y21,00000ffh
		mov player, 1
		mov t21, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next4		
	turn_x4:
		make_x x21,y21,0
		mov player, 2	
		mov t21, 1
		call verify_x
		cmp eax, 1
		je win_x
	next4:
	
	mov eax, [ebp+arg2]
	cmp eax, x22+1
	jl next5
	cmp eax, x22+80
	jg next5
	mov eax, [ebp+arg3]
	cmp eax, y22+1
	jl next5
	cmp eax, y22+80
	jg next5
	cmp t22, 0
	jne next5
	cmp player, 1
	je turn_x5
		make_0 x22,y22,00000ffh
		mov player, 1
		mov t22, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next5		
	turn_x5:
		make_x x22,y22,0
		mov player, 2
		mov t22, 1
		call verify_x
		cmp eax, 1
		je win_x
	next5:
	
	mov eax, [ebp+arg2]
	cmp eax, x23+1
	jl next6
	cmp eax, x23+80
	jg next6
	mov eax, [ebp+arg3]
	cmp eax, y23+1
	jl next6
	cmp eax, y23+80
	jg next6
	cmp t23, 0
	jne next6
	cmp player, 1
	je turn_x6
		make_0 x23,y23,00000ffh
		mov player, 1
		mov t23, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next6		
	turn_x6:
		make_x x23,y23,0
		mov player, 2
		mov t23, 1
		call verify_x
		cmp eax, 1
		je win_x
	next6:
	
	mov eax, [ebp+arg2]
	cmp eax, x31+1
	jl next7
	cmp eax, x31+80
	jg next7
	mov eax, [ebp+arg3]
	cmp eax, y31+1
	jl next7
	cmp eax, y31+80
	jg next7
	cmp t31, 0
	jne next7
	cmp player, 1
	je turn_x7
		make_0 x31,y31,00000ffh
		mov player, 1
		mov t31, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next7		
	turn_x7:
		make_x x31,y31,0
		mov player, 2
		mov t31, 1
		call verify_x
		cmp eax, 1
		je win_x
	next7:
	
	mov eax, [ebp+arg2]
	cmp eax, x32+1
	jl next8
	cmp eax, x32+80
	jg next8
	mov eax, [ebp+arg3]
	cmp eax, y32+1
	jl next8
	cmp eax, y32+80
	jg next8
	cmp t32, 0
	jne next8
	cmp player, 1
	je turn_x8
		make_0 x32,y32,00000ffh
		mov player, 1
		mov t32, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next8		
	turn_x8:
		make_x x32,y32,0
		mov player, 2
		mov t32, 1
		call verify_x
		cmp eax, 1
		je win_x
	next8:
	
	mov eax, [ebp+arg2]
	cmp eax, x33+1
	jl next9
	cmp eax, x33+80
	jg next9
	mov eax, [ebp+arg3]
	cmp eax, y33+1
	jl next9
	cmp eax, y33+80
	jg next9
	cmp t33, 0
	jne next9
	cmp player, 1
	je turn_x9
		make_0 x33,y33,00000ffh
		mov player, 1
		mov t33, 2
		call verify_0
		cmp eax, 1
		je win_0
	jmp next9		
	turn_x9:
		make_x x33,y33,0
		mov player, 2
		mov t33, 1
		call verify_x
		cmp eax, 1
		je win_x	
	next9:
	call verify_draw
	cmp eax, 1
	je is_draw
	jmp evt_timer
	
	AI_move:
	call can_win
	cmp eax, 0
	je cant_win
		cmp eax, 12
		jne pos1
			mov player, 2
			make_x x12,y12,0
			mov t12, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
		pos1:
		cmp eax, 21
		jne pos2
			mov player, 2
			make_x x21,y21,0
			mov t21, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
		pos2:
		cmp eax, 22
		jne pos3
			mov player, 2
			make_x x22,y22,0
			mov t22, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
		pos3:
		cmp eax, 23
		jne pos4
			mov player, 2
			make_x x23,y23,0
			mov t23, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
		pos4:
		cmp eax, 32
		jne pos5
			mov player, 2
			make_x x32,y32,0
			mov t32, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
		pos5:
	cant_win:
	call can_prevent
		cmp eax, 1
		jne cant_prevent
			mov player, 2
			make_x x22,y22,0
			mov t22, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
	cant_prevent:
	
	cmp t12, 2
	jne not_32
	cmp t22, 2
	jmp not_32
		mov player, 2
			make_x x32, y32, 0
			mov t32, 1
			call verify_x
			cmp eax, 1
			je win_x
			jmp evt_timer
	not_32:
	cmp t11, 0
	jne next_corner1
		mov player, 2
		make_x x11,y11,0
		mov t11, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_corner1:
	cmp t13, 0
	jne next_corner2
		mov player, 2
		make_x x13,y13,0
		mov t13, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_corner2:
	cmp t31, 0
	jne next_corner3
		mov player, 2
		make_x x31,y31,0
		mov t31, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_corner3:
	cmp t33, 0
	jne next_pos1
		mov player, 2
		make_x x33,y33,0
		mov t33, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos1:
	cmp t22, 0
	jne next_pos2
		mov player, 2
		make_x x22,y22,0
		mov t22, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos2:
	jne next_pos3
		mov player, 2
		make_x x23,y23,0
		mov t23, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos3:
	jne next_pos4
		mov player, 2
		make_x x32,y32,0
		mov t32, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos4:
	jne next_pos6
		mov player, 2
		make_x x22,y22,0
		mov t22, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos6:
	jne next_pos7
		mov player, 2
		make_x x12,y12,0
		mov t12, 1
		call verify_x
		cmp eax, 1
		je win_x
		jmp evt_timer
	next_pos7:
	call verify_draw
	cmp eax, 1
	jne evt_timer
	
	is_draw:
		mov game_over, 1
		cmp AI, 1
		je nope1;
		make_text_macro 'D', area, aibutton_x+50, aibutton_y-80
		make_text_macro 'R', area, aibutton_x+60, aibutton_y-80
		make_text_macro 'A', area, aibutton_x+70, aibutton_y-80
		make_text_macro 'W', area, aibutton_x+80, aibutton_y-80
		nope1:
		jmp evt_timer
		mov AI, 0
	
	win_x:
		mov game_over, 1
		cmp AI, 1
		je nope;
		make_text_macro 'X', area, aibutton_x+50, aibutton_y-80
		make_text_macro 'W', area, aibutton_x+70, aibutton_y-80
		make_text_macro 'O', area, aibutton_x+80, aibutton_y-80
		make_text_macro 'N', area, aibutton_x+90, aibutton_y-80
		nope:
		jmp evt_timer
		mov AI, 0
	win_0:
		mov game_over, 1
		cmp AI, 1
		je nope2
		make_text_macro '0', area, aibutton_x+50, aibutton_y-80
		make_text_macro 'W', area, aibutton_x+70, aibutton_y-80
		make_text_macro 'O', area, aibutton_x+80, aibutton_y-80
		make_text_macro 'N', area, aibutton_x+90, aibutton_y-80
		nope2:
		mov AI, 0
evt_timer:
	inc counter
	inc counterReset
	cmp counterReset, 5
	je reset_fail
afisare_litere:
	;afisam valoarea counter-ului curent (sute, zeci si unitati)
	mov ebx, 10
	mov eax, counter
	;cifra unitatilor
	mov edx, 0
	div ebx
	add edx, '0'
	make_text_macro edx, area, 30, 10
	;cifra zecilor
	mov edx, 0
	div ebx
	add edx, '0'
	make_text_macro edx, area, 20, 10
	;cifra sutelor
	mov edx, 0
	div ebx
	add edx, '0'
	make_text_macro edx, area, 10, 10
	
	make_text_macro 'X', area, 290, 24
	make_text_macro ' ', area, 300, 24
	make_text_macro 'S', area, 310, 24
	make_text_macro 'I', area, 320, 24
	make_text_macro ' ', area, 330, 24
	make_text_macro '0', area, 340, 24

	make_text_macro 'S', area, 250, 54
	make_text_macro 'C', area, 260, 54
	make_text_macro 'U', area, 270, 54
	make_text_macro 'T', area, 280, 54
	make_text_macro 'U', area, 290, 54
	make_text_macro 'R', area, 300, 54
	make_text_macro 'I', area, 310, 54
	make_text_macro 'C', area, 320, 54
	make_text_macro 'I', area, 330, 54
	make_text_macro ' ', area, 340, 54
	make_text_macro 'V', area, 350, 54
	make_text_macro 'L', area, 360, 54
	make_text_macro 'A', area, 370, 54
	make_text_macro 'D', area, 380, 54
	
	;buton de reset
	make_text_macro 'R', area, 416, 100
	make_text_macro 'E', area, 426, 100
	make_text_macro 'S', area, 436, 100
	make_text_macro 'E', area, 446, 100
	make_text_macro 'T', area, 456, 100
	
	line_horizontal reset_x, reset_y, reset_width, 0
	line_horizontal reset_x, reset_y+reset_height, reset_width, 0
	line_vertical reset_x, reset_y, reset_height, 0
	line_vertical reset_x+reset_width, reset_y, reset_height, 0
	
	;buton de AI
	
	line_horizontal aibutton_x, aibutton_y, aibutton_width, 0
	line_horizontal aibutton_x, aibutton_y+aibutton_height, aibutton_width, 0
	line_vertical aibutton_x, aibutton_y, aibutton_height, 0
	line_vertical aibutton_x+aibutton_width, aibutton_y, aibutton_height, 0
	make_text_macro 'P', area, aibutton_x+60, aibutton_y+10
	make_text_macro 'L', area, aibutton_x+70, aibutton_y+10
	make_text_macro 'A', area, aibutton_x+80, aibutton_y+10
	make_text_macro 'Y', area, aibutton_x+90, aibutton_y+10
	
	make_text_macro 'V', area, aibutton_x+70, aibutton_y+30
	make_text_macro 'S', area, aibutton_x+80, aibutton_y+30
	
	make_text_macro 'C', area, aibutton_x+40, aibutton_y+50
	make_text_macro 'O', area, aibutton_x+50, aibutton_y+50
	make_text_macro 'M', area, aibutton_x+60, aibutton_y+50
	make_text_macro 'P', area, aibutton_x+70, aibutton_y+50
	make_text_macro 'U', area, aibutton_x+80, aibutton_y+50
	make_text_macro 'T', area, aibutton_x+90, aibutton_y+50
	make_text_macro 'E', area, aibutton_x+100, aibutton_y+50
	make_text_macro 'R', area, aibutton_x+110, aibutton_y+50
	
	;tabla
	line_horizontal table_x, table_y+79, table_size, 0
	line_horizontal table_x, table_y+80, table_size, 0
	line_horizontal table_x, table_y+81, table_size, 0
	
	line_horizontal table_x, table_y+159, table_size, 0
	line_horizontal table_x, table_y+160, table_size, 0
	line_horizontal table_x, table_y+161, table_size, 0
	
	line_vertical table_x+79, table_y, table_size, 0
	line_vertical table_x+80, table_y, table_size, 0
	line_vertical table_x+81, table_y, table_size, 0
	
	
	line_vertical table_x+159, table_y, table_size, 0
	line_vertical table_x+160, table_y, table_size, 0
	line_vertical table_x+161, table_y, table_size, 0
	
	line_horizontal table_x, table_y, table_size, 00000ffh
	line_horizontal table_x, table_y+table_size, table_size, 00000ffh
	line_vertical table_x, table_y, table_size, 00000ffh
	line_vertical table_x+table_size, table_y, table_size, 00000ffh
	
	line_horizontal table_x, table_y+1, table_size, 00000ffh
	line_horizontal table_x, table_y+table_size-1, table_size, 00000ffh
	line_vertical table_x+1, table_y, table_size, 00000ffh
	line_vertical table_x+table_size-1, table_y, table_size, 00000ffh
	
	line_horizontal table_x, table_y+2, table_size, 00000ffh
	line_horizontal table_x, table_y+table_size-2, table_size, 00000ffh
	line_vertical table_x+2, table_y, table_size, 00000ffh
	line_vertical table_x+table_size-2, table_y, table_size, 00000ffh
	
	; make_0 table_x, table_y, 00000ffh
	; make_X table_x, table_y+80, 0
	
final_draw:
	popa 
	mov esp, ebp
	pop ebp
	ret
draw endp

start:
	;alocam memorie pentru zona de desenat
	mov eax, area_width
	mov ebx, area_height
	mul ebx
	shl eax, 2
	push eax
	call malloc
	add esp, 4
	mov area, eax
	;apelam functia de desenare a ferestrei
	; typedef void (*DrawFunc)(int evt, int x, int y);
	; void __cdecl BeginDrawing(const char *title, int width, int height, unsigned int *area, DrawFunc draw);
	push offset draw
	push area
	push area_height
	push area_width
	push offset window_title
	call BeginDrawing
	add esp, 20
	
	;terminarea programului
	push 0
	call exit
end start
