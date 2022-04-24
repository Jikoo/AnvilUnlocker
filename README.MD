### About
AnvilUnlocker is a Bukkit plugin allowing anvils to be used past the normal level cap of 40. No permissions.

The level cap is set via configuration option `maximumCost`. Any integer from 41 to 32767 is valid.

### Caveats
* Due to client limitations, repair costs over 40 levels cannot be displayed in red if the client lacks the experience required to complete the repair. For simplicity and consistency, AnvilUnlocker uses an approach that entirely removes the mechanic of the cost turning red when the client lacks the required experience, even when the cost is under 40 levels.
* Due to the client's inability to display items' costs greater than a short's max value (32767) and subsequent wrapping around, the new maximum is capped to 32767 instead of allowing any valid int value.
