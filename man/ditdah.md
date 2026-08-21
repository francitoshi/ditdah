% DITDAH(1) | Ditdah Manual | v0.0.1 (2026-01-29)

# NAME
ditdah - The stupid app that turns your boring texts into annoying beeps and back… because sometimes you just need to feel like a 19th-century spy.

# SYNOPSIS
**ditdah** COMMAND [OPTIONS]

# DESCRIPTION
Ditdah is a command-line application that converts ASCII text to Morse code and vice versa, as well as generating and decoding audio representations of Morse code (CW). It supports text and audio input/output, allowing you to encode/decode messages or play/listen to Morse code signals.

# COMMANDS
**e**, **encode**  
    Convert ASCII text to Morse code.

**d**, **decode**  
    Convert Morse code to ASCII text.

**p**, **play**  
    Convert ASCII text to CW (audio).

**t**, **listen**  
    Convert CW (audio) to ASCII text.

# OPTIONS
**-i**, **--input**=*FILE*  
    Input file.  
    Default: stdin for text, microphone for audio.

**-o**, **--output**=*FILE*  
    Output file.  
    Default: stdout for text, speaker for audio.

**-w**, **--wpm**=*WPM*  
    Set words per minute (using PARIS).  
    Valid values: 4 - 60.  
    Default: 12.

**-e**, **--ewpm**=*WPM*  
    Set effective words per minute (Farnsworth spacing).  
    Valid values: 4 - 60.  
    Default: 12.

**-f**, **--freq**=*HZ*  
    Set tone frequency to HZ.  
    Valid values: 20 - 4000.  
    Default: 800.

**-v**, **--volume**=*PERCENT*  
    Set initial volume to PERCENT.  
    Valid values: 1 - 100.  
    Default: 70.

**-W**, **--wave**=*TYPE*  
    Wave type for audio.  
    Valid values: sine, square, sawtooth, triangle.  
    Default: sine.

**-s**, **--sample-rate**=*HZ*  
    Audio sample rate.  
    Valid values: 8000-48100.  
    Default: 44100.

**-h**, **--help**  
    Display this help message and exit.

**-L**, **--license**  
    Display license information and exit.

**-V**, **--version**  
    Display version information and exit.

# EXAMPLES
Encode ASCII text file to Morse code text file:  
    ditdah encode -i message.txt -o morse.txt

Encode from stdin to stdout:  
    ditdah encode < input.txt > output.txt

Decode Morse code text file to ASCII text file:  
    ditdah decode -i morse.txt -o message.txt

Generate audio file from ASCII text:  
    ditdah play -i message.txt -o morse.wav

Play ASCII text as Morse code through speakers:  
    ditdah play -i message.txt

Decode audio file to ASCII text:  
    ditdah listen -i recording.wav -o message.txt

Listen to microphone and decode to text file:  
    ditdah listen -o message.txt

Pipeline example: encode and decode:  
    echo "<SOS>" | ditdah encode | ditdah decode

# NOTES
- Text input accepts A-Z, 0-9, and common punctuation.  
- Morse code uses dots (.) and dashes (-) separated by spaces.  
- Letters are separated by spaces, words by " / ".  
- Audio format: WAV, 16-bit PCM, mono.

# AUTHOR
francitoshi@gmail.com

# COPYRIGHT
Copyright (c) 2026 francitoshi@gmail.com

# BUGS
Report bugs to: <https://github.com/francitoshi/ditdah/issues>
