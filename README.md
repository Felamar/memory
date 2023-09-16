# Coolest MOSS

> Working on Java JDK 8+

## Compilation

```bash
javac -nowarn *.java
```

## Run

```bash
java MemoryManagement.java commands memory.conf
```

## Experimental - MVC Migration Class Diagram

```mermaid
%% ControlPanel2 is now "MemoryView"
%% Kernel2 is now "MemoryModel"

classDiagram
    class MemoryManagement{
        +main()
    }

    note for MemoryModel "Before: Kernel2"
    class MemoryModel{
        -int runs
        -int run_cycles
        -int no_virtual_pages %% repeated
        -long block_page_size
        -long address_limit
        -bool do_file_log
        -bool do_stdout_log
        -byte address_radix$
        -MemoryView view
        -String output_file
        -String commands_path
        -String config_path
        -Vector~Page~ pages_vector
        -Vector~Instruction~ instruction_vector
        -String final LineSeparator$

        %% new
        +readArguments(): void
        +readCommandsFile(): void
        +readPropFile(): void

        %% init inside contructor
        +init(String commands_path, String config_path): void

        %% is useful?
        -printLogFile(String message): void

        +run(): void
        +step(): void
        +reset(): void
        +getPage(int pageNum): void
        +getRuns(): int
        +getRunCycles(): int

        %% rework to controller class
        %%+setControlPanel(ControlPanel2 control_panel): void

        %% new
        +getNoVirtualPages(): int

        %% rework from Common class
        %% String to long
        +str2long(String s): long$

        %% String to integer
        +str2int(String s): int$

        %% String to binary
        +str2bin(String s): byte$

        +randomLong(long MAX): long$
    }

    note for MemoryView "Before: ControlPanel2"
    class MemoryView{
        %%-Kernel2 kernel %% used in controller
        -Button run_button
        -Button step_button
        -Button reset_button
        -Button exit_button
        -Label status_label
        -Label status_Label
        -Label time_Label
        -Label instruction_Label
        -Label address_Label
        -Label segmentation_Label
        -Label page_fault_Label
        -Label virtual_page_Label
        -Label physical_page_Label
        -Label referenced_Label
        -Label modified_Label
        -Label in_mem_time_Label
        -Label last_touch_time_Label
        -Label low_limit_address_Label
        -Label high_limit_address_Label

        %%+int no_virtual_pages %% repeated: get using getter in controller
        -Button[] button_pages
        -Label[] label_pages

        %% constructor
        +MemoryView(String title)

        %% rework: inside the constructor class
        %%+init(Kernel2 p_kernel, String commands, String config): void
        +paintPage(Page page): void
        +setStatus(String status): void
        +addPhysicalPage(int page_num, int physical_address): void
        +removePhysicalPage(int physical_address): void
    }

    Frame <|-- MemoryView

    class MemoryController{
        -MemoryView view
        -MemoryModel model

        %% ...In the future (maybe)...
        %% initSegmentMode(): void
        %% initPageMode(): void

        %% set view and set model
        +initSimulator(): void

        %% action listeners inside this class
        +initBottonsEvents(): void
    }

    %% As Utilities inside MemoryModel class
    %%class Common{
        %% String to long
        %%+s2l(String s): long$

        %% String to integer
        %%+s2i(String s): int$

        %% String to byte
        %%+s2b(String s): byte$

        %%+randomLong(long MAX): long$
    %%}

    note for Command "Before: Instruction"
    class Command{
        -String instruction

        %%new
        -String type

        -long min_address
        -long max_address

        Command(String instruction, String type, String min_address, String max_address): void
        Command(String instruction, long address)
        +getInstruction(): String
        +getMinAddress(): long
        +getMaxAddress(): long

        %% rework
        +isInRange(long MAX): bool

    }

    %% Not reworked yet
    class Page{
        -int ID
        -int physical_address
        -boolean referenced
        -boolean modified

        %% tiempo en memoria
        -int t_in_mem 

        -int t_since_touched
        -long up_mem_limit
        -long low_mem_limit

        Page(int ID, int physical_address, bool referenced, bool modified, int t_in_mem, int t_since_touched, long up_mem_limit, long low_mem_limit): void
        +getID(): int
        +getPhysicalAddress(): int
        +getReferenced(): bool
        +boolean getModified(): bool
        +getTimeInMemory(): int
        +getTimeSinceTouched(): int
        +getUpperMemoryLimit(): long
        +getLowerMemoryLimit(): long
        +setPhysicalAddress(): void
        +setReferenced(bool referenced): void
        +setModified(bool modified): void
        +setTimeInMemory(int t_in_mem): void
        +setTimeSinceTouched(int t_since_touched): void
        +setNewValues(String[] args, long no_virtual_pages): void
    }

    %% page replacement algorithm
    class PageFault{
        +replacePage(Vector mem, int virtPageNum, int replacePageNum, ControlPanel2 controlPanel): void$
    }

    MemoryManagement --> MemoryController
    MemoryController --> MemoryModel
    MemoryController --> MemoryView

    %%MemoryModel --> Common
    MemoryModel --> Command
    MemoryModel --> Page
    MemoryModel --> PageFault
```