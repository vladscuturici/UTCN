#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>

int convert_p (const char* p) {
    int x, d[3];
    for(int i=0; i<3; i++) {
        x = 0;
        if(p[3*i] != '-')
            x += 4;
        if(p[3*i+1] != '-')
            x += 2;
        if(p[3*i+2] != '-')
            x += 1;
        d[i] = x;
    }
    int oct = d[2] + d[1] * 8 + d[0] * 64;
    return oct;
}

int string_to_int (char* s) {
    int n = 0;
    for(int i=0; i < strlen(s); i++) {
        n += (int)(s[i] - '0'); 
        n *= 10;
    }
    n /= 10;
    return n;
}

void display_files_rec(const char *path, const char* permissions, const char* name_starts_with) {
    //functie adaptata dupa problema din indrumator
    DIR *dir = NULL;
    struct dirent *entry = NULL;
    char fullPath[512];
    struct stat statbuf;

    dir = opendir(path);
    if(dir == NULL) {
        return;
    }
    while((entry = readdir(dir)) != NULL) {
        if(strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0) {
            snprintf(fullPath, 512, "%s/%s", path, entry->d_name);
            if(lstat(fullPath, &statbuf) == 0) {
                if(strcmp(name_starts_with, "0") == 0 && strcmp(permissions, "0") == 0)
                    printf("%s\n", fullPath);
                else {
                    if(strcmp(name_starts_with, "0") != 0 && strcmp(permissions, "0") == 0)
                    {
                        if(strncmp(entry->d_name, name_starts_with, strlen(name_starts_with)) == 0)
                            printf("%s\n", fullPath);
                    }
                    else if(strcmp(name_starts_with, "0") == 0  && strcmp(permissions, "0") != 0) {
                        if ((statbuf.st_mode &0777) == convert_p(permissions))
                            printf("%s\n", fullPath);
                    }
                    else {
                        if(strncmp(entry->d_name, name_starts_with, strlen(name_starts_with)) == 0 && ((statbuf.st_mode &0777) == convert_p(permissions)))
                            printf("%s\n", fullPath);
                    }
                    
                }
                if(S_ISDIR(statbuf.st_mode)) {
                    display_files_rec(fullPath, permissions, name_starts_with);
                }
            }
        }
    }
    closedir(dir);
}

void display_files(const char *path, const char* permissions, const char* name_starts_with) {
    //functie adaptata dupa problema din indrumator
    DIR *dir = NULL;
    struct dirent *entry = NULL;
    char fullPath[512];
    struct stat statbuf;

    dir = opendir(path);
    if(dir == NULL) {
        return;
    }
    while((entry = readdir(dir)) != NULL) {
        if(strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0) {
            snprintf(fullPath, 512, "%s/%s", path, entry->d_name);
            if(lstat(fullPath, &statbuf) == 0) {
                if(strcmp(name_starts_with, "0") == 0 && strcmp(permissions, "0") == 0)
                    printf("%s\n", fullPath);
                else {
                    if(strcmp(name_starts_with, "0") != 0 && strcmp(permissions, "0") == 0)
                    {
                        if(strncmp(entry->d_name, name_starts_with, strlen(name_starts_with)) == 0)
                            printf("%s\n", fullPath);
                    }
                    else if(strcmp(name_starts_with, "0") == 0  && strcmp(permissions, "0") != 0) {
                        if ((statbuf.st_mode &0777) == convert_p(permissions))
                            printf("%s\n", fullPath);
                    }
                    else {
                        if(strncmp(entry->d_name, name_starts_with, strlen(name_starts_with)) == 0 && ((statbuf.st_mode &0777) == convert_p(permissions)))
                            printf("%s\n", fullPath);
                    }
                    
                }
            }
        }
    }
    closedir(dir);
}

void display (const char *path, int recursive, const char* permissions, const char* name_starts_with) {
    DIR *dir = NULL;
    dir = opendir(path);
    if(dir == NULL) {
        printf("ERROR\ninvalid directory path\n");
        return;
    }
    closedir(dir);
    printf("SUCCESS\n");
    if(recursive)
        display_files_rec(path, permissions, name_starts_with);
    else
        display_files(path, permissions, name_starts_with);
}

//ERROR
//wrong magic|version|sect_nr|sect_types
//sect type 73 76 87 43 49 54 16

void parse (int fd) {
    char magic;
    int header_size, no_of_sections, version, sect_size, sect_offset, sect_type;
    char sect_name[19];
    lseek(fd, -3, SEEK_END);
    read(fd, &header_size, 2);
    read(fd, &magic, 1);
    //printf("%c\n", magic);
    //printf("%d", header_size);
    lseek(fd, -header_size, SEEK_END);
    read(fd, &version, 4);
    read(fd, &no_of_sections, 1);
	if (magic != 'f') {
		printf("ERROR\nwrong magic\n");
		return;
	}

    if (version < 49 || version > 76) {
        printf("ERROR\nwrong version\n");
        return;
    }

    if (no_of_sections < 3 || no_of_sections > 15) {
        printf("ERROR\nwrong sect_nr\n");
        return;
    }
    // SECT_NAME: 19
    // SECT_TYPE: 2
    // SECT_OFFSET: 4
    // SECT_SIZE: 4
    
    // SUCCESS
    // version=<version_number>
    // nr_sections=<no_of_sections>
    // section1: <NAME_1> <TYPE_1> <SIZE_1>
    // section2: <NAME_2> <TYPE_2> <SIZE_2>
    for(int i=1; i <= no_of_sections; i++) {
            read(fd, &sect_name, 19);
            read(fd, &sect_type, 2);
            read(fd, &sect_offset, 4);
            read(fd, &sect_size, 4);
            if (sect_type != 73 && sect_type != 76 && sect_type != 87 && sect_type != 43 && sect_type != 49 && sect_type != 54 && sect_type != 16) {
                printf("ERROR\nwrong sect_types\n");
                return;
            }
    }
    lseek(fd, -header_size, SEEK_END);
    read(fd, &version, 4);
    read(fd, &no_of_sections, 1);
    printf("SUCCESS\n");
    printf("version=%d\n", version);
    printf("nr_sections=%d\n", no_of_sections);
    for(int i=0; i < no_of_sections; i++) {
        read(fd, &sect_name, 19);
        read(fd, &sect_type, 2);
        read(fd, &sect_offset, 4);
        read(fd, &sect_size, 4);
        printf("section%d: %s %d %d\n", i+1, sect_name, sect_type, sect_size);
        //printf("%d\n", sect_offset);
    }
}

void extract (int fd, int section, int line) {
    char c;
    int lines=0, header_size, no_of_sections, sect_offset, sect_offset2;
    lseek(fd, -3, SEEK_END);
    read(fd, &header_size, 2);
    lseek(fd, -header_size+4, SEEK_END);
    read(fd, &no_of_sections, 1);
    if(section > no_of_sections) {
        printf("\nERROR\ninvalid section\n");
        return;
    }
    for(int i=1; i <= section; i++) {
            lseek(fd, 21, SEEK_CUR);
            read(fd, &sect_offset, 4);
            lseek(fd, 4, SEEK_CUR);
    }
    lseek(fd, 21, SEEK_CUR);
    read(fd, &sect_offset2, 4);
    lseek(fd, sect_offset, SEEK_SET);
    for(int i = 1; i<=sect_offset2 - sect_offset; i++) {
        read(fd, &c, 1);
        if(c == '\n')
            lines++;
    }
    char l[512];
    int i = 0;
    if(line > lines) {
        printf("ERROR\ninvalid line\n");
        return;
    }
    lseek(fd, sect_offset, SEEK_SET);
    while(i<(lines-line+1)) {
        read(fd, &c, 1);
        if(c == '\n')
            i++;
    }
    read(fd, &c, 1);
    l[0] = c;
    i=1;
    while(c != '\n') {
        read(fd, &c, 1);
        l[i]=c;
        i++;
    }
    printf("SUCCESS\n%s", l);
}

int t49;

void parse2 (int fd) {
    t49 = 0;
    char magic;
    int header_size, no_of_sections, version, sect_size, sect_offset, sect_type;
    char sect_name[19];
    lseek(fd, -3, SEEK_END);
    read(fd, &header_size, 2);
    read(fd, &magic, 1);
    lseek(fd, -header_size, SEEK_END);
    read(fd, &version, 4);
    read(fd, &no_of_sections, 1);
	if (magic != 'f') {
		return;
	}

    if (version < 49 || version > 76) {
        return;
    }

    if (no_of_sections < 3 || no_of_sections > 15) {
        return;
    }
    for(int i=1; i <= no_of_sections; i++) {
            read(fd, &sect_name, 19);
            read(fd, &sect_type, 2);
            read(fd, &sect_offset, 4);
            read(fd, &sect_size, 4);
            if (sect_type != 73 && sect_type != 76 && sect_type != 87 && sect_type != 43 && sect_type != 49 && sect_type != 54 && sect_type != 16) {
                return;
            }
            if (sect_type == 49)
                t49 = 1;
    }
    lseek(fd, -header_size, SEEK_END);
    read(fd, &version, 4);
    read(fd, &no_of_sections, 1);
    for(int i=0; i < no_of_sections; i++) {
        read(fd, &sect_name, 19);
        read(fd, &sect_type, 2);
        read(fd, &sect_offset, 4);
        read(fd, &sect_size, 4);
    }
}

int parse_49 (const char* path) {
    int fd = open(path, O_RDONLY);
    parse2(fd);
    close(fd);
    return t49;
}

void display_files_rec_49(const char *path) {
    DIR *dir = NULL;
    struct dirent *entry = NULL;
    char fullPath[512];
    struct stat statbuf;

    dir = opendir(path);
    if(dir == NULL) {
        return;
    }
    while((entry = readdir(dir)) != NULL) {
        if(strcmp(entry->d_name, ".") != 0 && strcmp(entry->d_name, "..") != 0) {
            snprintf(fullPath, 512, "%s/%s", path, entry->d_name);
            if(lstat(fullPath, &statbuf) == 0) {
                if(parse_49(fullPath))
                    printf("%s\n", fullPath);
                if(S_ISDIR(statbuf.st_mode)) {
                    display_files_rec_49(fullPath);
                }
            }
        }
    }
    closedir(dir);
}

int main(int argc, char **argv) {
    if (argc >= 2) {
        if (strcmp(argv[1], "variant") == 0) {
            printf("22909\n");
        }
        else if (strcmp(argv[1], "list") == 0) {
            int recursive = 0;
            char* permissions = "0";
            char* name_starts_with = "0";
            char* path = "0";
            for (int i = 2; i < argc; i++) {
                if (strcmp(argv[i], "recursive") == 0) {
                    recursive = 1;
                }
                else if (strncmp(argv[i], "permissions=", strlen("permissions=")) == 0) {
                    permissions = argv[i] + strlen("permissions=");
                }
                else if (strncmp(argv[i], "name_starts_with=", strlen("name_starts_with=")) == 0) {
                    name_starts_with = argv[i] + strlen("name_starts_with=");
                }
            }
            path = argv[argc-1] + strlen("path=");
            display(path, recursive, permissions, name_starts_with);
        }
        else if (strcmp(argv[1], "parse") == 0) {
            char* path = argv[2] + strlen("path=");
            int fd = open(path, O_RDONLY);
			if (fd == -1) 
				printf("ERROR\ninvalid file path\n");
			else {
				parse(fd);
            }					
            close(fd);
        }
        else if (strcmp(argv[1], "extract") == 0) {
            char* path = argv[2] + strlen("path=");
            char* ssection = argv[3] + strlen("section=");
			char* sline = argv[4] + strlen("line=");
            int section = string_to_int (ssection);
            int line = string_to_int (sline);
            int fd = open(path, O_RDONLY);
			if (fd == -1) 
				printf("ERROR\ninvalid file\n");
			else {
                //printf("%s %d %d", path, section, line);
                extract(fd, section, line);		
            }
            close(fd);
        }
        else if (strcmp(argv[1], "findall") == 0) {
            char* path = argv[2] + strlen("path=");
            //printf("%d\n", parse_49("./so_vlad/a1/test_root/x2r9p1uE.3EP"));
            DIR *dir = NULL;
            dir = opendir(path);
            if(dir == NULL) {
                printf("ERROR\ninvalid directory path\n");
            } else {
                printf("SUCCESS\n");
                display_files_rec_49(path);
            }
            closedir(dir);
        }
    }
    return 0;
}
