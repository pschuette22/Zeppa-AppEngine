<%@tag language="java" description="No Side Bar Page template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true" %>
<%@attribute name="footer" fragment="true" %>

<html>
	<head>
		<title>Zeppa</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<!--[if lte IE 8]><script src="../template/assets/js/ie/html5shiv.js"></script><![endif]-->
		<link rel="stylesheet" href="../template/assets/css/main.css" />
		<!--[if lte IE 8]><link rel="stylesheet" href="../template/assets/css/ie8.css" /><![endif]-->
		<!--[if lte IE 9]><link rel="stylesheet" href="../template/assets/css/ie9.css" /><![endif]-->
	</head>
	<body class="no-sidebar">
		<div id="page-wrapper">

			<!-- Header Wrapper -->
				<div id="header-wrapper">
					<div class="container">

						<!-- Header -->
							<header id="header">
								<div class="inner">

									<!-- Logo -->
										<h1><a href="home.jsp" id="logo">Zeppa</a></h1>

									<!-- Nav -->
										<nav id="nav">
											<ul>
												<li class="current_page_item"><a href="/dashboard">Home</a></li>
												<li>
													<a href="/events">Events</a>
													<!--  <ul>
														<li><a href="#">Lorem ipsum dolor</a></li>
														<li><a href="#">Magna phasellus</a></li>
														<li>
															<a href="#">Phasellus consequat</a>
															<ul>
																<li><a href="#">Lorem ipsum dolor</a></li>
																<li><a href="#">Phasellus consequat</a></li>
																<li><a href="#">Magna phasellus</a></li>
																<li><a href="#">Etiam dolore nisl</a></li>
															</ul>
														</li>
														<li><a href="#">Veroeros feugiat</a></li>
													</ul>-->
												</li>
												<li><a href="/analytics">Analytics</a></li>
												<li><a href="/billing">Billing</a></li>
												<li><a href="/account-settings">Account</a></li>
											</ul>
										</nav>

								</div>
							</header>

					</div>
				</div>

			<!-- Main Wrapper -->
				<div id="main-wrapper">
					<div class="wrapper style2">
						<div class="inner">
						
							<div class="container">
								<div id="content">

									<!-- Content -->
										<jsp:doBody/>
										<article>
											<header class="major">
												<h2>No Sidebar</h2>
												<p>Sidebars are not welcome here</p>
											</header>

											<span class="image featured"><img src="../template/images/pic08.jpg" alt="" /></span>

											<p>Phasellus quam turpis, feugiat sit amet ornare in, hendrerit in lectus.
											Praesent semper mod quis eget mi. Etiam eu ante risus. Aliquam erat volutpat.
											Aliquam luctus et mattis lectus sit amet pulvinar. Nam turpis nisi
											consequat etiam lorem ipsum dolor sit amet nullam. Phasellus quam turpis,
											feugiat sit amet ornare in, hendrerit in lectus. Praesent semper mod quis
											eget mi. Etiam eu ante risus. Aliquam erat volutpat. Aliquam luctus et
											mattis lectus sit amet pulvinar. Nam turpis nisi consequat etiam.</p>

											<h3>More intriguing information</h3>
											<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ac quam risus, at tempus
											justo. Sed dictum rutrum massa eu volutpat. Quisque vitae hendrerit sem. Pellentesque lorem felis,
											ultricies a bibendum id, bibendum sit amet nisl. Mauris et lorem quam. Maecenas rutrum imperdiet
											vulputate. Nulla quis nibh ipsum, sed egestas justo. Morbi ut ante mattis orci convallis tempor.
											Etiam a lacus a lacus pharetra porttitor quis accumsan odio. Sed vel euismod nisi. Etiam convallis
											rhoncus dui quis euismod. Maecenas lorem tellus, congue et condimentum ac, ullamcorper non sapien.
											Donec sagittis massa et leo semper a scelerisque metus faucibus. Morbi congue mattis mi.
											ultricies a bibendum id, bibendum sit amet nisl. Mauris et lorem quam. Maecenas rutrum imperdiet
											vulputate. Nulla quis nibh ipsum, sed egestas justo. Morbi ut ante mattis orci convallis tempor.
											Etiam a lacus a lacus pharetra porttitor quis accumsan odio. Sed vel euismod nisi. Etiam convallis
											rhoncus dui quis euismod. Maecenas lorem tellus, congue et condimentum ac, ullamcorper non sapien.
											Donec sagittis massa et leo semper a scelerisque metus faucibus. Morbi congue mattis mi.
											Phasellus sed nisl vitae risus tristique volutpat. Cras rutrum commodo luctus lorem ipsum
											dolor sit amet phasellus consequat.</p>

											<p>Phasellus odio risus, faucibus et viverra vitae, eleifend ac purus. Praesent mattis, enim
											quis hendrerit porttitor, sapien tortor viverra magna, sit amet rhoncus nisl lacus nec arcu.
											Suspendisse laoreet metus ut metus imperdiet interdum aliquam justo tincidunt. Mauris dolor urna,
											fringilla vel malesuada ac, dignissim eu mi. Praesent mollis massa ac nulla pretium pretium.
											Suspendisse laoreet metus ut metus imperdiet interdum aliquam justo tincidunt. Mauris dolor urna,
											ultricies a bibendum id, bibendum sit amet nisl. Mauris et lorem quam. Maecenas rutrum imperdiet
											vulputate. Nulla quis nibh ipsum, sed egestas justo. Morbi ut ante mattis orci convallis tempor.
											fringilla vel malesuada ac, dignissim eu mi. Praesent mollis massa ac nulla pretium pretium.
											Maecenas tortor mauris, consectetur pellentesque dapibus eget, tincidunt vitae arcu.
											Vestibulum purus augue, tincidunt sit amet iaculis id, porta eu purus sed feugiat lorem ipsum
											dolor nullam blandit tempus.</p>
										</article>

								</div>
							</div>
						</div>
					</div>
					
				</div>

			<!-- Footer Wrapper -->
				<div id="footer-wrapper">
				<jsp:invoke fragment="footer"/>
					<footer id="footer" class="container">
						<div class="row">
							<div class="3u 12u(mobile)">

								<!-- Links -->
									<section>
										<h2>Filler Links</h2>
										<ul class="divided">
											<li><a href="#">Quam turpis feugiat dolor</a></li>
											<li><a href="#">Amet ornare in hendrerit </a></li>
											<li><a href="#">Semper mod quisturpis nisi</a></li>
											<li><a href="#">Consequat etiam phasellus</a></li>
											<li><a href="#">Amet turpis, feugiat et</a></li>
											<li><a href="#">Ornare hendrerit lectus</a></li>
											<li><a href="#">Semper mod quis et dolore</a></li>
											<li><a href="#">Amet ornare in hendrerit</a></li>
											<li><a href="#">Consequat lorem phasellus</a></li>
											<li><a href="#">Amet turpis, feugiat amet</a></li>
											<li><a href="#">Semper mod quisturpis</a></li>
										</ul>
									</section>

							</div>
							<div class="3u 12u(mobile)">

								<!-- Links -->
									<section>
										<h2>More Filler</h2>
										<ul class="divided">
											<li><a href="#">Quam turpis feugiat dolor</a></li>
											<li><a href="#">Amet ornare in in lectus</a></li>
											<li><a href="#">Semper mod sed tempus nisi</a></li>
											<li><a href="#">Consequat etiam phasellus</a></li>
										</ul>
									</section>

								<!-- Links -->
									<section>
										<h2>Even More Filler</h2>
										<ul class="divided">
											<li><a href="#">Quam turpis feugiat dolor</a></li>
											<li><a href="#">Amet ornare hendrerit lectus</a></li>
											<li><a href="#">Semper quisturpis nisi</a></li>
											<li><a href="#">Consequat lorem phasellus</a></li>
										</ul>
									</section>

							</div>
							<div class="6u 12u(mobile)">

								<!-- About -->
									<section>
										<h2><strong>ZeroFour</strong> by HTML5 UP</h2>
										<p>Hi! This is <strong>ZeroFour</strong>, a free, fully responsive HTML5 site
										template by <a href="http://n33.co/">AJ</a> for <a href="http://html5up.net/">HTML5 UP</a>.
										It's <a href="http://html5up.net/license/">Creative Commons Attribution</a>
										licensed so use it for any personal or commercial project (just credit us
										for the design!).</p>
										<a href="#" class="button alt icon fa-arrow-circle-right">Learn More</a>
									</section>

								<!-- Contact -->
									<section>
										<h2>Get in touch</h2>
										<div>
											<div class="row">
												<div class="6u 12u(mobile)">
													<dl class="contact">
														<dt>Twitter</dt>
														<dd><a href="#">@untitled-corp</a></dd>
														<dt>Facebook</dt>
														<dd><a href="#">facebook.com/untitled</a></dd>
														<dt>WWW</dt>
														<dd><a href="#">untitled.tld</a></dd>
														<dt>Email</dt>
														<dd><a href="#">user@untitled.tld</a></dd>
													</dl>
												</div>
												<div class="6u 12u(mobile)">
													<dl class="contact">
														<dt>Address</dt>
														<dd>
															1234 Fictional Rd<br />
															Nashville, TN 00000-0000<br />
															USA
														</dd>
														<dt>Phone</dt>
														<dd>(000) 000-0000</dd>
													</dl>
												</div>
											</div>
										</div>
									</section>

							</div>
						</div>
						<div class="row">
							<div class="12u">
								<div id="copyright">
									<ul class="menu">
										<li>&copy; Untitled. All rights reserved</li><li>Design: <a href="http://html5up.net">HTML5 UP</a></li>
									</ul>
								</div>
							</div>
						</div>
					</footer>
				</div>

		</div>

		<!-- Scripts -->

			<script src="../template/assets/js/jquery.min.js"></script>
			<script src="../template/assets/js/jquery.dropotron.min.js"></script>
			<script src="../template/assets/js/skel.min.js"></script>
			<script src="../template/assets/js/skel-viewport.min.js"></script>
			<script src="../template/assets/js/util.js"></script>
			<!--[if lte IE 8]><script src="../template/assets/js/ie/respond.min.js"></script><![endif]-->
			<script src="../template/assets/js/main.js"></script>

	</body>
</html>