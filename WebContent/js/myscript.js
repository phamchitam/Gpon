$(document).on("click","a",function(e) {
					var group = e.target.id;
					if (group == "update") {
						$.ajax({
							url : "/Gpon/Update",
							beforeSend : function() {
								var loading;
								loading = '<img style="position: absolute; width: 500px; height: 300px; top: 230px; left: 350px" src="/Gpon/image/loading.gif" />';
								$("#content").html(loading);
							},
							success : function(str) {
								$("#content").html(str);
							}
						});
					} else if (group == "thongke"){
						$.ajax({
							url : "/Gpon/Thongke",
							beforeSend : function() {
								var loading;
								loading = '<img style="position: absolute; width: 500px; height: 300px; top: 230px; left: 350px" src="/Gpon/image/loading.gif" />';
								$("#content").html(loading);
							},
							success : function(str) {
								$("#content").html(str);
							}
						});
						
					} else {
						var filename = "/Gpon/jsp/ftth.jsp" + "?group=" + group;
						$.ajax({
									url : "/Gpon/CountGpon",
									data : "group=" + group,
									type : "POST",
									beforeSend : function() {
										var loading;
										loading = '<img style="position: absolute; width: 500px; height: 300px; top: 230px; left: 350px" src="/Gpon/image/loading.gif" />';
										$("#content").html(loading);
									},
									success : function(str) {
										$("#content").load(filename);
									}
								});
					}

				});
