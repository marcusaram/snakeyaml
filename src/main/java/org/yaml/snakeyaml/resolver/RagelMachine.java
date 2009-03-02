// line 1 "RagelMachine.rl"
/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

public class RagelMachine {
    // line 17 "RagelMachine.rl"

    
    
// line 13 "RagelMachine.java"
private static byte[] init__snakeyaml_actions_0()
{
	return new byte [] {
	    0,    1,    0
	};
}

private static final byte _snakeyaml_actions[] = init__snakeyaml_actions_0();


private static byte[] init__snakeyaml_key_offsets_0()
{
	return new byte [] {
	    0,    0,   10,   12,   13,   14,   15,   16,   17,   18,   20,   24,
	   25,   26,   28,   29,   30,   32,   33,   34,   35,   36,   38,   39,
	   40
	};
}

private static final byte _snakeyaml_key_offsets[] = init__snakeyaml_key_offsets_0();


private static char[] init__snakeyaml_trans_keys_0()
{
	return new char [] {
	   70,   78,   79,   84,   89,  102,  110,  111,  116,  121,   65,   97,
	   76,   83,   69,  108,  115,  101,   79,  111,   70,   78,  102,  110,
	   70,  102,   82,  114,   85,  117,   69,  101,   83,  115,   97,  111,
	  102,  110,  114,  101,    0
	};
}

private static final char _snakeyaml_trans_keys[] = init__snakeyaml_trans_keys_0();


private static byte[] init__snakeyaml_single_lengths_0()
{
	return new byte [] {
	    0,   10,    2,    1,    1,    1,    1,    1,    1,    2,    4,    1,
	    1,    2,    1,    1,    2,    1,    1,    1,    1,    2,    1,    1,
	    0
	};
}

private static final byte _snakeyaml_single_lengths[] = init__snakeyaml_single_lengths_0();


private static byte[] init__snakeyaml_range_lengths_0()
{
	return new byte [] {
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0
	};
}

private static final byte _snakeyaml_range_lengths[] = init__snakeyaml_range_lengths_0();


private static byte[] init__snakeyaml_index_offsets_0()
{
	return new byte [] {
	    0,    0,   11,   14,   16,   18,   20,   22,   24,   26,   29,   34,
	   36,   38,   41,   43,   45,   48,   50,   52,   54,   56,   59,   61,
	   63
	};
}

private static final byte _snakeyaml_index_offsets[] = init__snakeyaml_index_offsets_0();


private static byte[] init__snakeyaml_indicies_0()
{
	return new byte [] {
	    0,    2,    3,    4,    5,    6,    7,    8,    9,   10,    1,   11,
	   12,    1,   13,    1,   14,    1,   15,    1,   16,    1,   17,    1,
	   15,    1,   15,   15,    1,   18,   15,   19,   15,    1,   15,    1,
	   15,    1,   20,   21,    1,   14,    1,   17,    1,   22,   23,    1,
	   15,    1,   15,    1,   12,    1,   15,    1,   19,   15,    1,   21,
	    1,   23,    1,    1,    0
	};
}

private static final byte _snakeyaml_indicies[] = init__snakeyaml_indicies_0();


private static byte[] init__snakeyaml_trans_targs_0()
{
	return new byte [] {
	    2,    0,    9,   10,   13,   16,   19,   20,   21,   22,   23,    3,
	    6,    4,    5,   24,    7,    8,   11,   12,   14,   15,   17,   18
	};
}

private static final byte _snakeyaml_trans_targs[] = init__snakeyaml_trans_targs_0();


private static byte[] init__snakeyaml_trans_actions_0()
{
	return new byte [] {
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
	};
}

private static final byte _snakeyaml_trans_actions[] = init__snakeyaml_trans_actions_0();


private static byte[] init__snakeyaml_eof_actions_0()
{
	return new byte [] {
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    1
	};
}

private static final byte _snakeyaml_eof_actions[] = init__snakeyaml_eof_actions_0();


static final int snakeyaml_start = 1;
static final int snakeyaml_error = 0;

static final int snakeyaml_en_main = 1;

// line 20 "RagelMachine.rl"

    public String scan(String scalar) {
        if (scalar == null) {
            throw new NullPointerException("Scalar must be provided");
        }
        String tag = null;
        int cs = 0;
        int p = 0;
        int pe = scalar.length();
        int eof = pe;
        byte[] data;
        if (pe == 0) {
            // NULL value
            data = new byte[] { (byte)'~' };
            pe = 1;
        } else {
            data = scalar.getBytes();
        }
        
// line 159 "RagelMachine.java"
	{
	cs = snakeyaml_start;
	}

// line 164 "RagelMachine.java"
	{
	int _klen;
	int _trans = 0;
	int _keys;
	int _goto_targ = 0;

	_goto: while (true) {
	switch ( _goto_targ ) {
	case 0:
	if ( p == pe ) {
		_goto_targ = 4;
		continue _goto;
	}
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
case 1:
	_match: do {
	_keys = _snakeyaml_key_offsets[cs];
	_trans = _snakeyaml_index_offsets[cs];
	_klen = _snakeyaml_single_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + _klen - 1;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + ((_upper-_lower) >> 1);
			if ( data[p] < _snakeyaml_trans_keys[_mid] )
				_upper = _mid - 1;
			else if ( data[p] > _snakeyaml_trans_keys[_mid] )
				_lower = _mid + 1;
			else {
				_trans += (_mid - _keys);
				break _match;
			}
		}
		_keys += _klen;
		_trans += _klen;
	}

	_klen = _snakeyaml_range_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + (_klen<<1) - 2;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + (((_upper-_lower) >> 1) & ~1);
			if ( data[p] < _snakeyaml_trans_keys[_mid] )
				_upper = _mid - 2;
			else if ( data[p] > _snakeyaml_trans_keys[_mid+1] )
				_lower = _mid + 2;
			else {
				_trans += ((_mid - _keys)>>1);
				break _match;
			}
		}
		_trans += _klen;
	}
	} while (false);

	_trans = _snakeyaml_indicies[_trans];
	cs = _snakeyaml_trans_targs[_trans];

case 2:
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
	if ( ++p != pe ) {
		_goto_targ = 1;
		continue _goto;
	}
case 4:
	if ( p == eof )
	{
	int __acts = _snakeyaml_eof_actions[cs];
	int __nacts = (int) _snakeyaml_actions[__acts++];
	while ( __nacts-- > 0 ) {
		switch ( _snakeyaml_actions[__acts++] ) {
	case 0:
// line 9 "RagelMachine.rl"
	{ tag = "tag:yaml.org,2002:bool"; }
	break;
// line 255 "RagelMachine.java"
		}
	}
	}

case 5:
	}
	break; }
	}
// line 42 "RagelMachine.rl"


        return tag;
    }
}