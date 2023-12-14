/*
Basic demo function to make an ajax call to get some demo data
*/
function getData( url, div ) {
	$.get( url, function( _data ) {
		try {
			if (typeof _data === 'string' || _data instanceof String) {
				var data = $.trim( _data );
				var data = _data.replace( /\n|\r/g, "<br/>" );			
				$( div ).html( data );			
			}
			else {				
				$( div ).html( JSON.stringify( _data ) );			
			}
		}
		catch(err) {
			//For a real app this of course has to be improved to check if in fact the 
			//response received is a SSO request
			$( "#ajax-data" ).html( err );
		}
	});	
}